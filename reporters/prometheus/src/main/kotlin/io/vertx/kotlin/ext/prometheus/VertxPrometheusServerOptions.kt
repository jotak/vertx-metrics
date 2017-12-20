package io.vertx.kotlin.ext.prometheus

import io.vertx.ext.prometheus.VertxPrometheusServerOptions

/**
 * A function providing a DSL for building [io.vertx.ext.prometheus.VertxPrometheusServerOptions] objects.
 *
 * Vert.x Prometheus embedded server configuration.
 *
 * @param endpoint 
 * @param host 
 * @param port 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.prometheus.VertxPrometheusServerOptions original] using Vert.x codegen.
 */
fun VertxPrometheusServerOptions(
  endpoint: String? = null,
  host: String? = null,
  port: Int? = null): VertxPrometheusServerOptions = io.vertx.ext.prometheus.VertxPrometheusServerOptions().apply {

  if (endpoint != null) {
    this.setEndpoint(endpoint)
  }
  if (host != null) {
    this.setHost(host)
  }
  if (port != null) {
    this.setPort(port)
  }
}

