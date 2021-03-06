/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Implementation of the standard JDK formatter that formats a message in GemFire's log format.
 */
public class GemFireFormatter extends Formatter {

  private final DateFormat dateFormat = DateFormatter.createDateFormat();

  public GemFireFormatter() {
    // nothing
  }

  @Override
  public String format(final LogRecord record) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.println();
    pw.print('[');
    pw.print(record.getLevel().getName());
    pw.print(' ');
    pw.print(dateFormat.format(new Date(record.getMillis())));
    String threadName = Thread.currentThread().getName();
    if (threadName != null) {
      pw.print(' ');
      pw.print(threadName);
    }
    pw.print(" tid=0x");
    pw.print(Long.toHexString(Thread.currentThread().getId()));
    pw.print("] ");
    pw.print("(msgTID=");
    pw.print(record.getThreadID());

    pw.print(" msgSN=");
    pw.print(record.getSequenceNumber());
    pw.print(") ");

    String msg = record.getMessage();
    if (msg != null) {
      try {
        LogWriterImpl.formatText(pw, msg, 40);
      } catch (RuntimeException e) {
        pw.println(msg);
        pw.println("Ignoring the following exception:");
        e.printStackTrace(pw);
      }
    } else {
      pw.println();
    }
    if (record.getThrown() != null) {
      record.getThrown().printStackTrace(pw);
    }
    pw.close();
    try {
      sw.close();
    } catch (IOException ignore) {
    }
    return sw.toString();
  }
}
