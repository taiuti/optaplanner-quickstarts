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
    protected Point pickup;
    protected Point delivery;

    public Location() {
    }

    public Location(long id, String name, Point pickup, Point delivery) {
        this.id = id;
        this.name = name;
        this.pickup = pickup;
        this.delivery = (delivery == null) ? pickup : delivery;
    }

    public Location(long id, String name, Point pickup) {
        this.id = id;
        this.name = name;
        this.pickup = pickup;
        this.delivery = pickup ;
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

    public Point getPickup() {
        return pickup;
    }

    public void setPickup(Point pickup) {
        this.pickup = pickup;
    }

    public Point getDelivery() {

        return delivery;
    }

    public void setDelivery(Point delivery) {
        this.delivery = delivery;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * The distance's unit of measurement depends on the
     * {@link VehicleRoutingSolution}'s {@link DistanceType}. It can be in miles or
     * km, but for most cases it's in the TSPLIB's unit of measurement.
     *
     * @return a positive number, the distance multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public abstract long getDistancePickDelivery();

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
        double latitudeDifference = (location.pickup.getLatitude() + location.delivery.getLatitude()) / 2
                - (pickup.getLatitude() + delivery.getLatitude()) / 2;
        double longitudeDifference = (location.pickup.getLongitude() + location.delivery.getLongitude()) / 2
                - (pickup.getLongitude() + delivery.getLongitude()) / 2;

        return Math.atan2(latitudeDifference, longitudeDifference);
    }

    @Override
    public String toString() {
        return pickup.toString() + " " + delivery.toString();
    }

}
