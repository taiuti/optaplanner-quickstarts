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

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.Depot;
import org.acme.vehiclerouting.domain.Vehicle;
import org.acme.vehiclerouting.domain.VehicleRoutingSolution;
import org.acme.vehiclerouting.domain.location.AirLocation;
import org.acme.vehiclerouting.domain.location.DistanceType;
import org.acme.vehiclerouting.domain.location.Location;
import org.acme.vehiclerouting.domain.location.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DemoDataBuilder {

        private static final AtomicLong sequence = new AtomicLong();

        private Location southWestCorner;
        private Location northEastCorner;
        private int rideCount;
        private int vehicleCount;
        private int depotCount;
        private int minDemand;
        private int maxDemand;
        private int vehicleCapacity;

        private DemoDataBuilder() {
        }

        public DemoDataBuilder setSouthWestCorner(Location southWestCorner) {
                this.southWestCorner = southWestCorner;
                return this;
        }

        public DemoDataBuilder setNorthEastCorner(Location northEastCorner) {
                this.northEastCorner = northEastCorner;
                return this;
        }

        public DemoDataBuilder setMinDemand(int minDemand) {
                this.minDemand = minDemand;
                return this;
        }

        public DemoDataBuilder setMaxDemand(int maxDemand) {
                this.maxDemand = maxDemand;
                return this;
        }

        public DemoDataBuilder setRideCount(int rideCount) {
                this.rideCount = rideCount;
                return this;
        }

        public DemoDataBuilder setVehicleCount(int vehicleCount) {
                this.vehicleCount = vehicleCount;
                return this;
        }

        public DemoDataBuilder setDepotCount(int depotCount) {
                this.depotCount = depotCount;
                return this;
        }

        public DemoDataBuilder setVehicleCapacity(int vehicleCapacity) {
                this.vehicleCapacity = vehicleCapacity;
                return this;
        }

        public static DemoDataBuilder builder() {
                return new DemoDataBuilder();
        }

        public VehicleRoutingSolution build() {

                if (minDemand < 1) {
                        throw new IllegalStateException("minDemand (" + minDemand + ") must be greater than zero.");
                }
                if (maxDemand < 1) {
                        throw new IllegalStateException("maxDemand (" + maxDemand + ") must be greater than zero.");
                }
                if (minDemand >= maxDemand) {
                        throw new IllegalStateException("maxDemand (" + maxDemand + ") must be greater than minDemand ("
                                        + minDemand + ").");
                }
                if (vehicleCapacity < 1) {
                        throw new IllegalStateException("Number of vehicleCapacity (" + vehicleCapacity
                                        + ") must be greater than zero.");
                }
                if (rideCount < 1) {
                        throw new IllegalStateException(
                                        "Number of rideCount (" + rideCount + ") must be greater than zero.");
                }
                if (vehicleCount < 1) {
                        throw new IllegalStateException(
                                        "Number of vehicleCount (" + vehicleCount + ") must be greater than zero.");
                }
                if (depotCount < 1) {
                        throw new IllegalStateException(
                                        "Number of depotCount (" + depotCount + ") must be greater than zero.");
                }

                if (northEastCorner.getPickup().getLatitude() <= southWestCorner.getPickup().getLatitude()) {
                        throw new IllegalStateException("southWestCorner.getPickup().getLatitude ("
                                        + southWestCorner.getPickup().getLatitude()
                                        + ") must be greater than southWestCorner.getPickup().getLatitude("
                                        + southWestCorner.getPickup().getLatitude() + ").");
                }

                if (northEastCorner.getPickup().getLongitude() <= southWestCorner.getPickup().getLongitude()) {
                        throw new IllegalStateException("southWestCorner.getPickup().getLongitude ("
                                        + southWestCorner.getPickup().getLongitude()
                                        + ") must be greater than southWestCorner.getPickup().getLongitude("
                                        + southWestCorner.getPickup().getLongitude() + ").");
                }

                String name = "demo";
                DistanceType distanceType = DistanceType.AIR_DISTANCE;
                String distanceUnitOfMeasurement = "km";

                Random random = new Random(2);
                PrimitiveIterator.OfDouble latitudes = random.doubles(southWestCorner.getPickup().getLatitude(),
                                northEastCorner.getPickup().getLatitude()).iterator();
                PrimitiveIterator.OfDouble longitudes = random.doubles(southWestCorner.getPickup().getLongitude(),
                                northEastCorner.getPickup().getLongitude()).iterator();

                PrimitiveIterator.OfInt demand = random.ints(minDemand, maxDemand).iterator();

                PrimitiveIterator.OfInt depotRandom = random.ints(0, depotCount).iterator();

                Supplier<Depot> depotSupplier = () -> new Depot(sequence.incrementAndGet(),
                                new AirLocation(sequence.incrementAndGet(), "", new Point(sequence.incrementAndGet(),
                                                "", latitudes.nextDouble(), longitudes.nextDouble())));

                List<Depot> depotList = Stream.generate(depotSupplier).limit(depotCount).collect(Collectors.toList());

                Supplier<Vehicle> vehicleSupplier = () -> new Vehicle(sequence.incrementAndGet(), vehicleCapacity,
                                depotList.get(depotRandom.nextInt()));

                List<Vehicle> vehicleList = Stream.generate(vehicleSupplier).limit(vehicleCount)
                                .collect(Collectors.toList());

                Supplier<Ride> rideSupplier = () -> new Ride(sequence.incrementAndGet(),
                                new AirLocation(sequence.incrementAndGet(), "",
                                                new Point(sequence.incrementAndGet(), "", latitudes.nextDouble(),
                                                                longitudes.nextDouble()),
                                                new Point(sequence.incrementAndGet(), "", latitudes.nextDouble(),
                                                                longitudes.nextDouble())),
                                demand.nextInt());

                List<Ride> rideList = Stream.generate(rideSupplier).limit(rideCount)
                                .collect(Collectors.toList());

                List<Location> locationList = new ArrayList<Location>();
                for (Ride ride : rideList) {

                        locationList.add(ride.getLocation());
                }

                for (Depot depot : depotList) {
                        locationList.add(depot.getLocation());
                }

                return new VehicleRoutingSolution(name, distanceType, distanceUnitOfMeasurement, locationList,
                                depotList, vehicleList, rideList, southWestCorner, northEastCorner);
        }

}
