/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package netty.error;

/**
 * The base class of all other Amethyst exceptions
 *
 * @author xiongjie001
 * @version v0.1 2017-11-13 15:33 xiongjie001 Exp $
 */
public class AmethystException extends RuntimeException {

    private final static long serialVersionUID = 1L;

    public AmethystException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmethystException(String message) {
        super(message);
    }

    public AmethystException(Throwable cause) {
        super(cause);
    }

    public AmethystException() {
        super();
    }

}
