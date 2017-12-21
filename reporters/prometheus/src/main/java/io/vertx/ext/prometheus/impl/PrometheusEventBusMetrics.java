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
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.spi.metrics.EventBusMetrics;

/**
 * @author Joel Takvorian
 */
class PrometheusEventBusMetrics implements EventBusMetrics<PrometheusEventBusMetrics.Handler> {
  private final Gauge handlers;
  private final Gauge pending;
  private final Counter published;
  private final Counter sent;
  private final Counter received;
  private final Counter delivered;
  private final Counter errorCount;
  private final Counter replyFailures;
  private final Summary processTime;
  private final Summary bytesRead;
  private final Summary bytesWritten;

  PrometheusEventBusMetrics(CollectorRegistry registry) {
    handlers = Gauge.build("vertx_eventbus_handlers", "Number of event bus handlers in use")
      .labelNames(Labels.ADDRESS)
      .register(registry);
    pending = Gauge.build("vertx_eventbus_pending", "Number of messages not processed yet")
      .labelNames(Labels.ADDRESS, Labels.SIDE)
      .register(registry);
    published = Counter.build("vertx_eventbus_published", "Number of messages published (publish / subscribe)")
      .labelNames(Labels.ADDRESS, Labels.SIDE)
      .register(registry);
    sent = Counter.build("vertx_eventbus_sent", "Number of messages sent (point-to-point)")
      .labelNames(Labels.ADDRESS, Labels.SIDE)
      .register(registry);
    received = Counter.build("vertx_eventbus_received", "Number of messages received")
      .labelNames(Labels.ADDRESS, Labels.SIDE)
      .register(registry);
    delivered = Counter.build("vertx_eventbus_delivered", "Number of messages delivered to handlers")
      .labelNames(Labels.ADDRESS, Labels.SIDE)
      .register(registry);
    errorCount = Counter.build("vertx_eventbus_errors", "Number of errors")
      .labelNames(Labels.ADDRESS, Labels.CLASS)
      .register(registry);
    replyFailures = Counter.build("vertx_eventbus_reply_failures", "Number of message reply failures")
      .labelNames(Labels.ADDRESS, "failure")
      .register(registry);
    processTime = Summary.build("vertx_eventbus_processing_time", "Processing time")
      .labelNames(Labels.ADDRESS)
      .register(registry);
    bytesRead = Summary.build("vertx_eventbus_bytes_read", "Number of bytes received while reading messages from event bus cluster peers")
      .labelNames(Labels.ADDRESS)
      .register(registry);
    bytesWritten = Summary.build("vertx_eventbus_bytes_written", "Number of bytes sent while sending messages to event bus cluster peers")
      .labelNames(Labels.ADDRESS)
      .register(registry);
  }

  @Override
  public Handler handlerRegistered(String address, String repliedAddress) {
    handlers.labels(address).inc();
    return new Handler(address);
  }

  @Override
  public void handlerUnregistered(Handler handler) {
    handlers.labels(handler.address).dec();
  }

  @Override
  public void scheduleMessage(Handler handler, boolean b) {
  }

  @Override
  public void beginHandleMessage(Handler handler, boolean local) {
    pending.labels(handler.address, Labels.getSide(local)).dec();
    handler.timer = processTime.labels(handler.address).startTimer();
  }

  @Override
  public void endHandleMessage(Handler handler, Throwable failure) {
    handler.timer.observeDuration();
    if (failure != null) {
      errorCount.labels(handler.address, failure.getClass().getSimpleName()).inc();
    }
  }

  @Override
  public void messageSent(String address, boolean publish, boolean local, boolean remote) {
    if (publish) {
      published.labels(address, Labels.getSide(local)).inc();
    } else {
      sent.labels(address, Labels.getSide(local)).inc();
    }
  }

  @Override
  public void messageReceived(String address, boolean publish, boolean local, int handlers) {
    String origin = Labels.getSide(local);
    pending.labels(address, origin).inc(handlers);
    received.labels(address, origin).inc();
    if (handlers > 0) {
      delivered.labels(address, origin).inc();
    }
  }

  @Override
  public void messageWritten(String address, int numberOfBytes) {
    bytesWritten.labels(address).observe(numberOfBytes);
  }

  @Override
  public void messageRead(String address, int numberOfBytes) {
    bytesRead.labels(address).observe(numberOfBytes);
  }

  @Override
  public void replyFailure(String address, ReplyFailure failure) {
    replyFailures.labels(address, failure.name()).inc();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void close() {
  }

  public static class Handler {
    private final String address;
    private Summary.Timer timer;

    Handler(String address) {
      this.address = address;
    }
  }
}
