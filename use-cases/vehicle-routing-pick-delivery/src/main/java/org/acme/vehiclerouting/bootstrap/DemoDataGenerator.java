/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
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
 */

package org.acme.vehiclerouting.bootstrap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.acme.vehiclerouting.domain.VehicleRoutingSolution;
import org.acme.vehiclerouting.domain.location.AirLocation;
import org.acme.vehiclerouting.domain.location.Point;
import org.acme.vehiclerouting.persistence.VehicleRoutingSolutionRepository;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {

    private final VehicleRoutingSolutionRepository repository;

    public DemoDataGenerator(VehicleRoutingSolutionRepository repository) {
        this.repository = repository;
    }

    public void generateDemoData(@Observes StartupEvent startupEvent) {

                Integer rideCount = 77;
                Integer vehicleCount = 2;
                Integer depotCount = 1;
                Integer minDemand = 1;
                Integer maxDemand = 2;
                Integer vehicleCapacity = 40;
                
                VehicleRoutingSolution problem = DemoDataBuilder.builder().setMinDemand(minDemand).setMaxDemand(maxDemand)
                        .setVehicleCapacity(vehicleCapacity).setRideCount(rideCount).setVehicleCount(vehicleCount)
                        .setDepotCount(depotCount)
                        .setSouthWestCorner(new AirLocation(0L, "", new Point(0L, "", 43.751466, 11.177210)))
                        .setNorthEastCorner(new AirLocation(0L, "", new Point(0L, "", 43.809291, 11.290195))).build();
        
        repository.update(problem);
    }
}
