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
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.core.spi.metrics.TCPMetrics;
import io.vertx.ext.prometheus.VertxPrometheusOptions;

/**
 * @author Joel Takvorian
 */
class PrometheusNetServerMetrics {
  private final Gauge connections;
  private final Histogram bytesReceived;
  private final Histogram bytesSent;
  private final Counter errorCount;
  final boolean hasRemoteLabel;

  PrometheusNetServerMetrics(VertxPrometheusOptions options, CollectorRegistry registry) {
    this(options, registry, "vertx_net");
  }

  PrometheusNetServerMetrics(VertxPrometheusOptions options, CollectorRegistry registry, String prefix) {
    if (options.isEnableRemoteLabelForServers()) {
      hasRemoteLabel = true;
      connections = Gauge.build(prefix + "_server_connections", "Number of opened connections to the server")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      bytesReceived = Histogram.build(prefix + "_server_bytes_received", "Number of bytes received by the server")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      bytesSent = Histogram.build(prefix + "_server_bytes_sent", "Number of bytes sent by the server")
        .labelNames(Labels.LOCAL, Labels.REMOTE)
        .register(registry);
      errorCount = Counter.build(prefix + "_server_errors", "Number of errors")
        .labelNames(Labels.LOCAL, Labels.REMOTE, Labels.CLASS)
        .register(registry);
    } else {
      hasRemoteLabel = false;
      connections = Gauge.build(prefix + "_server_connections", "Number of opened connections to the server")
        .labelNames(Labels.LOCAL)
        .register(registry);
      bytesReceived = Histogram.build(prefix + "_server_bytes_received", "Number of bytes received by the server")
        .labelNames(Labels.LOCAL)
        .register(registry);
      bytesSent = Histogram.build(prefix + "_server_bytes_sent", "Number of bytes sent by the server")
        .labelNames(Labels.LOCAL)
        .register(registry);
      errorCount = Counter.build(prefix + "_server_errors", "Number of errors")
        .labelNames(Labels.LOCAL, Labels.CLASS)
        .register(registry);
    }
  }

  TCPMetrics forAddress(SocketAddress localAddress) {
    String local = Labels.fromAddress(localAddress);
    return new Instance(local);
  }

  class Instance implements TCPMetrics<String> {
    final String local;

    Instance(String local) {
      this.local = local;
    }

    @Override
    public String connected(SocketAddress remoteAddress, String remoteName) {
      String remote = Labels.fromAddress(new SocketAddressImpl(remoteAddress.port(), remoteName));
      if (hasRemoteLabel) {
        connections.labels(local, remote).inc();
      } else {
        connections.labels(local).inc();
      }
      return remote;
    }

    @Override
    public void disconnected(String remote, SocketAddress remoteAddress) {
      if (hasRemoteLabel) {
        connections.labels(local, remote).dec();
      } else {
        connections.labels(local).dec();
      }
    }

    @Override
    public void bytesRead(String remote, SocketAddress remoteAddress, long numberOfBytes) {
      if (hasRemoteLabel) {
        bytesReceived.labels(local, remote).observe(numberOfBytes);
      } else {
        bytesReceived.labels(local).observe(numberOfBytes);
      }
    }

    @Override
    public void bytesWritten(String remote, SocketAddress remoteAddress, long numberOfBytes) {
      if (hasRemoteLabel) {
        bytesSent.labels(local, remote).observe(numberOfBytes);
      } else {
        bytesSent.labels(local).observe(numberOfBytes);
      }
    }

    @Override
    public void exceptionOccurred(String remote, SocketAddress remoteAddress, Throwable t) {
      if (hasRemoteLabel) {
        errorCount.labels(local, remote, t.getClass().getSimpleName()).inc();
      } else {
        errorCount.labels(local, t.getClass().getSimpleName()).inc();
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
}
