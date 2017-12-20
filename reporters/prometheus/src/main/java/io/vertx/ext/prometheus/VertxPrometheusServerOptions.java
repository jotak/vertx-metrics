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
package io.vertx.ext.prometheus;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Vert.x Prometheus embedded server configuration.
 *
 * @author Joel Takvorian
 */
@DataObject(generateConverter = true, inheritConverter = true)
public class VertxPrometheusServerOptions {

  private static final String DEFAULT_ENDPOINT = "/metrics";
  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 9090;

  private String endpoint;
  private int port;
  private String host;

  /**
   * Default server on localhost:9090. Metrics are exposed on /metrics by default.
   */
  public VertxPrometheusServerOptions() {
    endpoint = DEFAULT_ENDPOINT;
    port = DEFAULT_PORT;
    host = DEFAULT_HOST;

  }

  public VertxPrometheusServerOptions(VertxPrometheusServerOptions other) {
    endpoint = other.endpoint != null ? other.endpoint : DEFAULT_ENDPOINT;
    host = other.host != null ? other.host : DEFAULT_HOST;
    port = other.port;
  }

  public VertxPrometheusServerOptions(JsonObject json) {
    this();
    VertxPrometheusServerOptionsConverter.fromJson(json, this);
  }

  /**
   * Set server port
   * @param port HTTP port
   */
  public VertxPrometheusServerOptions port(int port) {
    this.port = port;
    return this;
  }

  /**
   * Set server host
   * @param host HTTP host
   */
  public VertxPrometheusServerOptions host(String host) {
    this.host = host;
    return this;
  }

  /**
   * Set metrics endpoint
   * @param endpoint metrics endpoint
   */
  public VertxPrometheusServerOptions endpoint(String endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setHost(String host) {
    this.host = host;
  }
}
