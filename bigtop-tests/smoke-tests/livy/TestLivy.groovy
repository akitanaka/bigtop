/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.bigtop.itest.hadoop.livy

import org.apache.bigtop.itest.shell.Shell
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.junit.Test

import static org.junit.Assert.assertEquals

class TestLivy {
  static private Log LOG = LogFactory.getLog(Object.class)

  static Shell sh = new Shell("/bin/bash -s")

  static String cmdPrefix = "export HADOOP_CONF_DIR=/etc/hadoop/conf;HADOOP_USER_NAME=hdfs"
  static private final String config_file = "/etc/livy/conf/livy.conf";


  private static void execCommand(String cmd) {
    LOG.info(cmd)

    sh.exec("$cmdPrefix $cmd")
  }

  @Test
  void testCheckRestfulAPI() {
    // read Livy port address
    execCommand("awk '{if(/^livy.server.port/) print \$2}' < "+ config_file);
    String port = sh.out.join('\n');
    if (port.equals("")) {
      port = "8998";
    }
    execCommand("awk '{if(/^livy.server.host:/) print \$2}' < "+config_file);
    String host = sh.out.join('\n');
    if (host.equals("")) {
      host = "127.0.0.1";
    }
    // check web API
    execCommand("curl http://"+host+":"+port+"/ui");
    final String result = sh.out.join('\n');
    assert(result.contains("Livy"));
  }
}
