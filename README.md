<!--
   Licensed to ObjectStyle LLC under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ObjectStyle LLC licenses
   this file to you under the Apache License, Version 2.0 (the
   “License”); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
  -->

[![build test deploy](https://github.com/bootique/bootique-cxf/actions/workflows/maven.yml/badge.svg)](https://github.com/bootique/bootique-cxf/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.bootique.cxf/bootique-cxf-core.svg?colorB=brightgreen)](https://search.maven.org/artifact/io.bootique.cxf/bootique-cxf-core/)

# bootique-cxf

Provides [Apache CXF](https://cxf.apache.org/) integration with [Bootique](http://bootique.io).

CXF can be used in a different contexts:
- [JAX-WS Server](bootique-cxf-jaxws-server)
- [JAX-WS Client](bootique-cxf-jaxws-client)
- [JAX-RS Server](bootique-cxf-jaxrs)

All of those modules can be used simultaneously in a single application. 