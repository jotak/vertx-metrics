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
import io.prometheus.client.Summary;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.ext.prometheus.VertxPrometheusOptions;

/**
 * @author Joel Takvorian
 */
class PrometheusHttpServerMetrics extends PrometheusNetServerMetrics {
  private final Gauge requests;
  private final Counter requestCount;
  private final Counter requestResetCount;
  private final Summary processingTime;
  private final Gauge wsConnections;

  PrometheusHttpServerMetrics(VertxPrometheusOptions options, CollectorRegistry registry) {
    super(options, registry, "vertx_http");
    if (hasRemoteLabel) {
      requests = Gauge.build("vertx_http_server_requests", "Number of requests being processed")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      requestCount = Counter.build("vertx_http_server_request_count", "Number of processed requests")
        .labelNames(Labels.LOCAL, Labels.REMOTE, Labels.METHOD, Labels.CODE)
        .register(registry);
      requestResetCount = Counter.build("vertx_http_server_request_reset_count", "Number of requests reset")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      processingTime = Summary.build("vertx_http_server_reponse_time", "Request processing time")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      wsConnections = Gauge.build("vertx_http_server_ws_connections", "Number of websockets currently opened")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
    } else {
      requests = Gauge.build("vertx_http_server_requests", "Number of requests being processed")
        .labelNames(Labels.LOCAL)
        .register(registry);
      requestCount = Counter.build("vertx_http_server_request_count", "Number of processed requests")
        .labelNames(Labels.LOCAL, Labels.METHOD, Labels.CODE)
        .register(registry);
      requestResetCount = Counter.build("vertx_http_server_request_reset_count", "Number of requests reset")
        .labelNames(Labels.LOCAL)
        .register(registry);
      processingTime = Summary.build("vertx_http_server_reponse_time", "Request processing time")
        .labelNames(Labels.LOCAL)
        .register(registry);
      wsConnections = Gauge.build("vertx_http_server_ws_connections", "Number of websockets currently opened")
        .labelNames(Labels.LOCAL)
        .register(registry);
    }
  }

  @Override
  HttpServerMetrics forAddress(SocketAddress localAddress) {
    String local = Labels.fromAddress(localAddress);
    return new Instance(local);
  }

  class Instance extends PrometheusNetServerMetrics.Instance implements HttpServerMetrics<Handler, String, String> {
    Instance(String local) {
      super(local);
    }

    @Override
    public Handler requestBegin(String remote, HttpServerRequest request) {
      Handler handler = new Handler(remote, request.method().name());
      if (hasRemoteLabel) {
        requests.labels(local, remote).inc();
        handler.timer = processingTime.labels(local, remote).startTimer();
      } else {
        requests.labels(local).inc();
        handler.timer = processingTime.labels(local).startTimer();
      }
      return handler;
    }

    @Override
    public void requestReset(Handler handler) {
      if (hasRemoteLabel) {
        requestResetCount.labels(local, handler.address).inc();
        requests.labels(local, handler.address).dec();
      } else {
        requestResetCount.labels(local).inc();
        requests.labels(local).dec();
      }
    }

    @Override
    public Handler responsePushed(String remote, HttpMethod method, String uri, HttpServerResponse response) {
      Handler handler = new Handler(remote, method.name());
      if (hasRemoteLabel) {
        requests.labels(local, remote).inc();
      } else {
        requests.labels(local).inc();
      }
      return handler;
    }

    @Override
    public void responseEnd(Handler handler, HttpServerResponse response) {
      handler.timer.observeDuration();
      if (hasRemoteLabel) {
        requestCount.labels(local, handler.address, handler.method, String.valueOf(response.getStatusCode())).inc();
        requests.labels(local, handler.address).dec();
      } else {
        requestCount.labels(local, handler.method, String.valueOf(response.getStatusCode())).inc();
        requests.labels(local).dec();
      }
    }

    @Override
    public String upgrade(Handler handler, ServerWebSocket serverWebSocket) {
      return handler.address;
    }

    @Override
    public String connected(String remote, ServerWebSocket serverWebSocket) {
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
    private final String method;
    private Summary.Timer timer;

    Handler(String address, String method) {
      this.address = address;
      this.method = method;
    }
  }
}
