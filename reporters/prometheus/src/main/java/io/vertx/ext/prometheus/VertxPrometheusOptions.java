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
import io.vertx.ext.metrics.collector.MetricsOptionsBase;
import io.vertx.ext.prometheus.impl.PrometheusVertxMetrics;

/**
 * Vert.x Prometheus monitoring configuration.
 * If no embedded server is used, you can bind an existing {@link io.vertx.ext.web.Router} with {@link PrometheusVertxMetrics#createMetricsHandler()}<br/>
 * Ex:<br/>
 * {@code myRouter.route("/metrics").handler(PrometheusVertxMetrics.createMetricsHandler());}
 *
 * @author Joel Takvorian
 */
@DataObject(generateConverter = true, inheritConverter = true)
public class VertxPrometheusOptions extends MetricsOptionsBase {

  private VertxPrometheusServerOptions serverOptions;
  private boolean separateRegistry;
  private boolean enableRemoteLabelForClients;
  private boolean enableRemoteLabelForServers;

  public VertxPrometheusOptions() {
    enableRemoteLabelForServers = true;
  }

  public VertxPrometheusOptions(VertxPrometheusOptions other) {
    super(other);
    separateRegistry = other.separateRegistry;
    enableRemoteLabelForClients = other.enableRemoteLabelForClients;
    enableRemoteLabelForServers = other.enableRemoteLabelForServers;
    if (other.serverOptions != null) {
      serverOptions = new VertxPrometheusServerOptions(other.serverOptions);
    }
  }

  public VertxPrometheusOptions(JsonObject json) {
    this();
    VertxPrometheusOptionsConverter.fromJson(json, this);
  }

  /**
   * Set whether metrics will be enabled on the Vert.x instance. Metrics are not enabled by default.
   */
  @Override
  public VertxPrometheusOptions setEnabled(boolean enable) {
    super.setEnabled(enable);
    return this;
  }

  /**
   * An embedded server will start to expose metrics with Prometheus format
   * @param serverOptions the server options
   */
  public VertxPrometheusOptions embedServer(VertxPrometheusServerOptions serverOptions) {
    this.serverOptions = serverOptions;
    return this;
  }

  public VertxPrometheusServerOptions getServerOptions() {
    return serverOptions;
  }

  public void setServerOptions(VertxPrometheusServerOptions serverOptions) {
    this.serverOptions = serverOptions;
  }

  public void separateRegistry() {
    this.separateRegistry = true;
  }

  public boolean isSeparateRegistry() {
    return separateRegistry;
  }

  public void setSeparateRegistry(boolean separateRegistry) {
    this.separateRegistry = separateRegistry;
  }

  public boolean isEnableRemoteLabelForClients() {
    return enableRemoteLabelForClients;
  }

  /**
   * Set false to prevent generation of a label named "remote" on client-related metrics, used to group data points per remote.
   * This is relevant when the application makes client connections to a large number of different clients,
   * in order to reduce the number of related prometheus metrics created.<br/>
   * This option is set to <i>true</i> by default.
   */
  public void setEnableRemoteLabelForClients(boolean enableRemoteLabelForClients) {
    this.enableRemoteLabelForClients = enableRemoteLabelForClients;
  }

  public boolean isEnableRemoteLabelForServers() {
    return enableRemoteLabelForServers;
  }

  /**
   * Set true to allow generation of a label named "remote" on server-related metrics, used to group data points per remote.
   * This is relevant when the number of clients connecting to the application servers is small and under control,
   * in order to reduce the number of related prometheus metrics created.<br/>
   * This option is set to <i>false</i> by default.
   */
  public void setEnableRemoteLabelForServers(boolean enableRemoteLabelForServers) {
    this.enableRemoteLabelForServers = enableRemoteLabelForServers;
  }
}
