/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.ext.influxdb;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.ext.influxdb.AuthenticationOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.influxdb.AuthenticationOptions} original class using Vert.x codegen.
 */
public class AuthenticationOptionsConverter {

  public static void fromJson(JsonObject json, AuthenticationOptions obj) {
    if (json.getValue("enabled") instanceof Boolean) {
      obj.setEnabled((Boolean)json.getValue("enabled"));
    }
    if (json.getValue("secret") instanceof String) {
      obj.setSecret((String)json.getValue("secret"));
    }
    if (json.getValue("username") instanceof String) {
      obj.setUsername((String)json.getValue("username"));
    }
  }

  public static void toJson(AuthenticationOptions obj, JsonObject json) {
    json.put("enabled", obj.isEnabled());
    if (obj.getSecret() != null) {
      json.put("secret", obj.getSecret());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}