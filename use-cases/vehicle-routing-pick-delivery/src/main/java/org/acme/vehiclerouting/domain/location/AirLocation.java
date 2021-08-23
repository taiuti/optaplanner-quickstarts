/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

/**
 * The cost between 2 locations is a straight line: the euclidean distance
 * between their GPS coordinates. Used with {@link DistanceType#AIR_DISTANCE}.
 */

public class AirLocation extends Location {

    public AirLocation() {
    }

    public AirLocation(long id, String name, Point pickup, Point delivery) {
        super(id, name, pickup, delivery);
    }

    public AirLocation(long id, String name, Point pickup) {
        super(id, name, pickup);
    }

    protected Long getAirDistanceDoubleTo(Location location) {
        // Implementation specified by TSPLIB
        // http://www2.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a
        // sphere

        // l1 = (a1,b1) -> (c1,d1)
        // l2 = (a2,b2) -> (c2,d2)
        // distance L1 to L2 = Sqrt((c1-a1)^2 + (d1-b1)^2) + Sqrt((a2-c1)^2 + (b2-d1)^2)
        // + Sqrt((c2-a2)^2 + (d2-b2)^2)
        Double latDiff = location.getPickup().getLatitude() - this.getDelivery().getLatitude();
        Double longDiff = location.getPickup().getLongitude() - this.getDelivery().getLongitude();
        Double d = Math.sqrt((latDiff * latDiff) + (longDiff * longDiff));

        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        long distance = (long) (d * 1000.0 + 0.5);

        distance = this.getDistancePickDelivery() + location.getDistancePickDelivery() + distance;

        return distance;
    }

    @Override
    public long getDistanceTo(Location location) {
        long distance = getAirDistanceDoubleTo(location);
        return distance;
    }

    @Override
    public long getDistancePickDelivery() {

        Double latDiff = this.getDelivery().getLatitude() - this.getPickup().getLatitude();
        Double longDiff = this.getDelivery().getLongitude() - this.getPickup().getLongitude();
        Double distance = Math.sqrt((latDiff * latDiff) + (longDiff * longDiff));

        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

}
