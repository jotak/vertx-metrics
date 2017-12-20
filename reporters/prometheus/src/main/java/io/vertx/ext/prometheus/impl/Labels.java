/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertx.ext.prometheus.impl;

import io.vertx.core.net.SocketAddress;

/**
 * @author Joel Takvorian
 */
public final class Labels {
  public static final String LOCAL = "local";
  public static final String REMOTE = "remote";
  public static final String CLASS = "class";
  public static final String ADDRESS = "address";
  public static final String SIDE = "side";
  public static final String METHOD = "method";
  public static final String CODE = "code";

  private Labels() {
  }

  public static String fromAddress(SocketAddress address) {
    return address == null ? "?" : (address.host() + ":" + address.port());
  }

  public static String getSide(boolean local) {
    return local ? LOCAL : REMOTE;
  }
}
