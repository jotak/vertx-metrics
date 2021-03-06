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

package io.vertx.ext.metrics.collector;

import io.vertx.core.Handler;
import io.vertx.ext.metrics.collector.impl.DataPoint;

import java.util.List;

/**
 * A reporter from metrics data.
 *
 * @author Thomas Segismont
 */
public interface Reporter extends Handler<List<DataPoint>> {
  /**
   * Invoked when the managed Vert.x instance shuts down.
   */
  void stop();
}
