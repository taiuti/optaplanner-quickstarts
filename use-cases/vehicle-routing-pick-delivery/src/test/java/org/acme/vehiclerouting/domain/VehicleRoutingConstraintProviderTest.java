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

package org.acme.vehiclerouting.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.vehiclerouting.domain.location.AirLocation;
import org.acme.vehiclerouting.domain.location.Location;
import org.acme.vehiclerouting.domain.location.Point;
import org.acme.vehiclerouting.domain.timewindowed.TimeWindowedRide;
import org.acme.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import javax.inject.Inject;

@QuarkusTest
class VehicleRoutingConstraintProviderTest {

    @Inject
    ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier;
    private final Location location1 = new AirLocation(1L, "", new Point(0L, "", 0.0, 0.0));
    private final Location location2 = new AirLocation(2L, "", new Point(0L, "", 0.0, 4.0));
    private final Location location3 = new AirLocation(3L, "", new Point(0L, "", 3.0, 0.0));

    @Test
    public void vehicleCapacityUnpenalized() {
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));
        Ride ride1 = new Ride(2L, location2, 80);
        ride1.setPreviousStandstill(vehicleA);
        ride1.setVehicle(vehicleA);
        vehicleA.setNextRide(ride1);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity).given(vehicleA, ride1)
                .penalizesBy(0);
    }

    @Test
    public void vehicleCapacityPenalized() {
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));
        Ride ride1 = new Ride(2L, location2, 80);
        ride1.setPreviousStandstill(vehicleA);
        ride1.setVehicle(vehicleA);
        vehicleA.setNextRide(ride1);
        Ride ride2 = new Ride(3L, location3, 40);
        ride2.setPreviousStandstill(ride1);
        ride2.setVehicle(vehicleA);
        ride1.setNextRide(ride2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(vehicleA, ride1, ride2).penalizesBy(20);
    }

    @Test
    public void distanceToPreviousStandstill() {
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));
        Ride ride1 = new Ride(2L, location2, 80);
        ride1.setPreviousStandstill(vehicleA);
        ride1.setVehicle(vehicleA);
        vehicleA.setNextRide(ride1);
        Ride ride2 = new Ride(3L, location3, 40);
        ride2.setPreviousStandstill(ride1);
        ride2.setVehicle(vehicleA);
        ride1.setNextRide(ride2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::distanceToPreviousStandstill)
                .given(vehicleA, ride1, ride2).penalizesBy(9000L);
    }

    @Test
    public void distanceFromLastRideToDepot() {
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));
        Ride ride1 = new Ride(2L, location2, 80);
        ride1.setPreviousStandstill(vehicleA);
        ride1.setVehicle(vehicleA);
        vehicleA.setNextRide(ride1);
        Ride ride2 = new Ride(3L, location3, 40);
        ride2.setPreviousStandstill(ride1);
        ride2.setVehicle(vehicleA);
        ride1.setNextRide(ride2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::distanceFromLastRideToDepot)
                .given(vehicleA, ride1, ride2).penalizesBy(3000L);
    }

    @Test
    public void arrivalAfterDueTime() {
        Vehicle vehicleA = new Vehicle(1L, 100, new TimeWindowedDepot(1L, location1, 8_00_00L, 18_00_00L));
        TimeWindowedRide ride1 = new TimeWindowedRide(2L, location2, 1, 8_00_00L, 18_00_00L, 1_00_00L);
        ride1.setPreviousStandstill(vehicleA);
        ride1.setVehicle(vehicleA);
        vehicleA.setNextRide(ride1);
        ride1.setArrivalTime(8_00_00L + 4000L);
        TimeWindowedRide ride2 = new TimeWindowedRide(3L, location3, 40, 8_00_00L, 9_00_00L, 1_00_00L);
        ride2.setPreviousStandstill(ride1);
        ride2.setVehicle(vehicleA);
        ride1.setNextRide(ride2);
        ride2.setArrivalTime(8_00_00L + 4000L + 1_00_00L + 5000L);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::arrivalAfterDueTime)
                .given(vehicleA, ride1, ride2).penalizesBy(90_00L);
    }

}
