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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.acme.vehiclerouting.bootstrap.DemoDataBuilder;
import org.acme.vehiclerouting.domain.location.AirLocation;
import org.acme.vehiclerouting.domain.location.DistanceType;
import org.acme.vehiclerouting.domain.location.Location;
import org.acme.vehiclerouting.domain.location.Point;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class VehicleRoutingSolution {

    protected String name;
    protected DistanceType distanceType;
    protected String distanceUnitOfMeasurement;

    @ProblemFactCollectionProperty
    protected List<Location> locationList;

    @ProblemFactCollectionProperty
    protected List<Depot> depotList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "vehicleRange")
    protected List<Vehicle> vehicleList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "rideRange")
    protected List<Ride> rideList;

    @PlanningScore
    protected HardSoftLongScore score;

    protected Location southWestCorner;
    protected Location northEastCorner;

    public VehicleRoutingSolution() {
    }

    public VehicleRoutingSolution(String name, DistanceType distanceType, String distanceUnitOfMeasurement,
            List<Location> locationList, List<Depot> depotList, List<Vehicle> vehicleList, List<Ride> rideList,
            Location southWestCorner, Location northEastCorner) {
        this.name = name;
        this.distanceType = distanceType;
        this.distanceUnitOfMeasurement = distanceUnitOfMeasurement;
        this.locationList = locationList;
        this.depotList = depotList;
        this.vehicleList = vehicleList;
        this.rideList = rideList;
        this.southWestCorner = southWestCorner;
        this.northEastCorner = northEastCorner;
    }

    public static VehicleRoutingSolution empty() {

        VehicleRoutingSolution problem = DemoDataBuilder.builder().setMinDemand(1).setMaxDemand(2)
        .setVehicleCapacity(15).setRideCount(77).setVehicleCount(6).setDepotCount(2)
        .setSouthWestCorner(new AirLocation(0L, "", new Point(0L, "", 43.751466, 11.177210)))
        .setNorthEastCorner(new AirLocation(0L, "", new Point(0L, "", 43.809291, 11.290195))).build();

        problem.setScore(HardSoftLongScore.ZERO);

        return problem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public String getDistanceUnitOfMeasurement() {
        return distanceUnitOfMeasurement;
    }

    public void setDistanceUnitOfMeasurement(String distanceUnitOfMeasurement) {
        this.distanceUnitOfMeasurement = distanceUnitOfMeasurement;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Depot> getDepotList() {
        return depotList;
    }

    public void setDepotList(List<Depot> depotList) {
        this.depotList = depotList;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<Ride> getRideList() {
        return rideList;
    }

    public void setRideList(List<Ride> rideList) {
        this.rideList = rideList;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public List<Location> getBounds() {
        return Arrays.asList(southWestCorner, northEastCorner);
    }

    public String getDistanceString(NumberFormat numberFormat) {
        if (score == null) {
            return null;
        }
        long distance = getDistance();
        if (distanceUnitOfMeasurement == null) {
            return numberFormat.format(((double) distance) / 1000.0);
        }
        switch (distanceUnitOfMeasurement) {
            case "sec": // TODO why are the values 1000 larger?
                long hours = distance / 3600000L;
                long minutes = distance % 3600000L / 60000L;
                long seconds = distance % 60000L / 1000L;
                long milliseconds = distance % 1000L;
                return hours + "h " + minutes + "m " + seconds + "s " + milliseconds + "ms";
            case "km": { // TODO why are the values 1000 larger?
                long km = distance / 10L;
                long meter = distance % 10L;
                return km + "km " + meter + "m";
            }
            case "meter": {
                long km = distance / 1000L;
                long meter = distance % 1000L;
                return km + "km " + meter + "m";
            }
            default:
                return numberFormat.format(((double) distance) / 1000.0) + " " + distanceUnitOfMeasurement;
        }
    }

    public Long getDistance() {

        Long distance=0L;

        for(Vehicle vehicle: vehicleList){
            distance += vehicle.getTotalDistance();
        }

        return distance;

    }

    public String getDistanceKm() {
        return getDistanceString(null);
    }

}
