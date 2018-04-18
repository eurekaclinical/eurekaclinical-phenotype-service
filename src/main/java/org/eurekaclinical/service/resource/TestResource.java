package org.eurekaclinical.service.resource;

/*-
 * #%L
 * eurekaclinical-phenotype-service
 * %%
 * Copyright (C) 2018 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.inject.persist.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by akalsan on 10/5/16.
 */


@Path("/protected/test")
@Transactional
public class TestResource {

    public TestResource() {
    }

    @GET
    public String getStringHello() {
        return "Hello World";
    }
}
