/*
 * Copyright (c) 2011-2017 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.prometheus.impl;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.ext.prometheus.VertxPrometheusOptions;

/**
 * @author Joel Takvorian
 */
class PrometheusHttpClientMetrics extends PrometheusNetClientMetrics {
  private final Gauge requests;
  private final Counter requestCount;
  private final Histogram responseTime;
  private final Counter responseCount;
  private final Gauge wsConnections;

  PrometheusHttpClientMetrics(VertxPrometheusOptions options, CollectorRegistry registry) {
    super(options, registry, "vertx_http");
    if (hasRemoteLabel) {
      requests = Gauge.build("vertx_http_client_requests", "Number of requests waiting for a response")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      requestCount = Counter.build("vertx_http_client_request_count", "Number of requests sent")
        .labelNames(Labels.LOCAL, Labels.REMOTE, Labels.METHOD)
        .register(registry);
      responseTime = Histogram.build("vertx_http_client_reponse_time", "Response time")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      responseCount = Counter.build("vertx_http_client_reponse_count", "Response count with codes")
        .labelNames(Labels.LOCAL, Labels.REMOTE, Labels.CODE)
        .register(registry);
      wsConnections = Gauge.build("vertx_http_client_ws_connections", "Number of websockets currently opened")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
    } else {
      requests = Gauge.build("vertx_http_client_requests", "Number of requests waiting for a response")
        .labelNames(Labels.LOCAL)
        .register(registry);
      requestCount = Counter.build("vertx_http_client_request_count", "Number of requests sent")
        .labelNames(Labels.LOCAL, Labels.METHOD)
        .register(registry);
      responseTime = Histogram.build("vertx_http_client_reponse_time", "Response time")
        .labelNames(Labels.LOCAL)
        .register(registry);
      responseCount = Counter.build("vertx_http_client_reponse_count", "Response count with codes")
        .labelNames(Labels.LOCAL, Labels.CODE)
        .register(registry);
      wsConnections = Gauge.build("vertx_http_client_ws_connections", "Number of websockets currently opened")
        .labelNames(Labels.LOCAL)
        .register(registry);
    }
  }

  @Override
  HttpClientMetrics forAddress(String localAddress) {
    return new Instance(localAddress);
  }

  class Instance extends PrometheusNetClientMetrics.Instance implements HttpClientMetrics<PrometheusHttpClientMetrics.Handler, String, String, Void, Void> {
    Instance(String localAddress) {
      super(localAddress);
    }

    @Override
    public Void createEndpoint(String host, int port, int maxPoolSize) {
      return null;
    }

    @Override
    public void closeEndpoint(String host, int port, Void endpointMetric) {
    }

    @Override
    public Void enqueueRequest(Void endpointMetric) {
      return null;
    }

    @Override
    public void dequeueRequest(Void endpointMetric, Void taskMetric) {
    }

    @Override
    public void endpointConnected(Void endpointMetric, String socketMetric) {
    }

    @Override
    public void endpointDisconnected(Void endpointMetric, String socketMetric) {
    }

    @Override
    public Handler requestBegin(Void endpointMetric, String remote, SocketAddress localAddress, SocketAddress remoteAddress, HttpClientRequest request) {
      Handler handler = new Handler(remote);
      if (hasRemoteLabel) {
        requests.labels(local, remote).inc();
        requestCount.labels(local, remote, request.method().name()).inc();
        handler.timer = responseTime.labels(local, remote).startTimer();
      } else {
        requests.labels(local).inc();
        requestCount.labels(local, request.method().name()).inc();
        handler.timer = responseTime.labels(local).startTimer();
      }
      return handler;
    }

    @Override
    public void requestEnd(Handler requestMetric) {
    }

    @Override
    public void responseBegin(Handler requestMetric, HttpClientResponse response) {
    }

    @Override
    public Handler responsePushed(Void endpointMetric, String remote, SocketAddress localAddress, SocketAddress remoteAddress, HttpClientRequest request) {
      return requestBegin(null, remote, localAddress, remoteAddress, request);
    }

    @Override
    public void requestReset(Handler handler) {
      if (hasRemoteLabel) {
        requests.labels(local, handler.address).dec();
      } else {
        requests.labels(local).dec();
      }
    }

    @Override
    public void responseEnd(Handler handler, HttpClientResponse response) {
      if (hasRemoteLabel) {
        requests.labels(local, handler.address).dec();
        responseCount.labels(local, handler.address, String.valueOf(response.statusCode()));
      } else {
        requests.labels(local).dec();
        responseCount.labels(local, String.valueOf(response.statusCode()));
      }
      handler.timer.observeDuration();
    }

    @Override
    public String connected(Void endpointMetric, String remote, WebSocket webSocket) {
      if (hasRemoteLabel) {
        wsConnections.labels(local, remote).inc();
      } else {
        wsConnections.labels(local).inc();
      }
      return remote;
    }

    @Override
    public void disconnected(String remote) {
      if (hasRemoteLabel) {
        wsConnections.labels(local, remote).dec();
      } else {
        wsConnections.labels(local).dec();
      }
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void close() {
    }
  }

  public static class Handler {
    private final String address;
    private Histogram.Timer timer;

    Handler(String address) {
      this.address = address;
    }
  }
}
