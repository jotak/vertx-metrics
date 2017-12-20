/*
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package examples;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.docgen.Source;
import io.vertx.ext.metrics.collector.MetricsType;
import io.vertx.ext.prometheus.VertxPrometheusOptions;
import io.vertx.ext.prometheus.VertxPrometheusServerOptions;
import io.vertx.ext.prometheus.impl.PrometheusVertxMetrics;
import io.vertx.ext.web.Router;

/**
 * @author Joel Takvorian
 */
@Source
@SuppressWarnings("unused")
public class MetricsExamples {

  Vertx vertx;

  public void setup() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new VertxPrometheusOptions().setEnabled(true)));
  }

  public void setupEmbeddedServer() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new VertxPrometheusOptions().setEnabled(true)
        .embedServer(new VertxPrometheusServerOptions())));
  }

  public void setupEmbeddedServerWithOptions() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new VertxPrometheusOptions().setEnabled(true)
        .embedServer(new VertxPrometheusServerOptions()
          .port(8080)
          .endpoint("/metrics/vertx"))));
  }

  public void setupBoundRouter() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new VertxPrometheusOptions().setEnabled(true)));

    // Later on, creating a router
    Router router = Router.router(vertx);
    router.route("/custom").handler(PrometheusVertxMetrics.createMetricsHandler());
  }

  public void setupDisabledMetricsTypes() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new VertxPrometheusOptions().setEnabled(true)
        .addDisabledMetricsType(MetricsType.HTTP_CLIENT)
        .addDisabledMetricsType(MetricsType.NET_CLIENT)));
  }
}
