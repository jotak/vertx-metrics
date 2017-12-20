package io.vertx.kotlin.ext.prometheus

import io.vertx.ext.prometheus.VertxPrometheusOptions
import io.vertx.ext.metrics.collector.MetricsType
import io.vertx.ext.prometheus.VertxPrometheusServerOptions

/**
 * A function providing a DSL for building [io.vertx.ext.prometheus.VertxPrometheusOptions] objects.
 *
 * Vert.x Prometheus monitoring configuration.
 * If no embedded server is used, you can bind an existing [io.vertx.ext.web.Router] with <br/>
 * Ex:<br/>
 * <code>myRouter.route("/metrics").handler(PrometheusVertxMetrics.createMetricsHandler());</code>
 *
 * @param disabledMetricsTypes 
 * @param enableRemoteLabelForClients  Set false to prevent generation of a label named "remote" on client-related metrics, used to group data points per remote. This is relevant when the application makes client connections to a large number of different clients, in order to reduce the number of related prometheus metrics created.<br/> This option is set to <i>true</i> by default.
 * @param enableRemoteLabelForServers  Set true to allow generation of a label named "remote" on server-related metrics, used to group data points per remote. This is relevant when the number of clients connecting to the application servers is small and under control, in order to reduce the number of related prometheus metrics created.<br/> This option is set to <i>false</i> by default.
 * @param enabled  Set whether metrics will be enabled on the Vert.x instance. Metrics are not enabled by default.
 * @param metricsBridgeAddress 
 * @param metricsBridgeEnabled 
 * @param separateRegistry 
 * @param serverOptions 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.prometheus.VertxPrometheusOptions original] using Vert.x codegen.
 */
fun VertxPrometheusOptions(
  disabledMetricsTypes: Iterable<MetricsType>? = null,
  enableRemoteLabelForClients: Boolean? = null,
  enableRemoteLabelForServers: Boolean? = null,
  enabled: Boolean? = null,
  metricsBridgeAddress: String? = null,
  metricsBridgeEnabled: Boolean? = null,
  separateRegistry: Boolean? = null,
  serverOptions: io.vertx.ext.prometheus.VertxPrometheusServerOptions? = null): VertxPrometheusOptions = io.vertx.ext.prometheus.VertxPrometheusOptions().apply {

  if (disabledMetricsTypes != null) {
    this.setDisabledMetricsTypes(disabledMetricsTypes.toSet())
  }
  if (enableRemoteLabelForClients != null) {
    this.setEnableRemoteLabelForClients(enableRemoteLabelForClients)
  }
  if (enableRemoteLabelForServers != null) {
    this.setEnableRemoteLabelForServers(enableRemoteLabelForServers)
  }
  if (enabled != null) {
    this.setEnabled(enabled)
  }
  if (metricsBridgeAddress != null) {
    this.setMetricsBridgeAddress(metricsBridgeAddress)
  }
  if (metricsBridgeEnabled != null) {
    this.setMetricsBridgeEnabled(metricsBridgeEnabled)
  }
  if (separateRegistry != null) {
    this.setSeparateRegistry(separateRegistry)
  }
  if (serverOptions != null) {
    this.setServerOptions(serverOptions)
  }
}

