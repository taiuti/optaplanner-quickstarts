/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.acme.vehiclerouting.domain.location;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonIgnoreProperties({ "id", "name" })
public abstract class Location {

    protected Long id = null;
    protected String name = null;
    protected Point start; // Scorta
    protected Point end; // Servizio

    public Location() {
    }

    public Location(long id, String name, Point start, Point end) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = (end == null) ? start : end;
    }

    public Location(long id, String name, Point start) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = start ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {

        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * The distance's unit of measurement depends on the
     * {@link VehicleRoutingSolution}'s {@link DistanceType}. It can be in miles or
     * km, but for most cases it's in the TSPLIB's unit of measurement.
     *
     * @param location never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public abstract long getDistanceTo(Location location);

    /**
     * The angle relative to the direction EAST.
     *
     * @param location never null
     * @return in Cartesian coordinates
     */
    public double getAngle(Location location) {
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a
        // sphere
        double latitudeDifference = (location.start.getLatitude() + location.end.getLatitude()) / 2
                - (start.getLatitude() + end.getLatitude()) / 2;
        double longitudeDifference = (location.start.getLongitude() + location.end.getLongitude()) / 2
                - (start.getLongitude() + end.getLongitude()) / 2;

        return Math.atan2(latitudeDifference, longitudeDifference);
    }

    @Override
    public String toString() {
        return start.toString() + " " + end.toString();
    }

}
