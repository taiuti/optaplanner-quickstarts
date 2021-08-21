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

package org.acme.vehiclerouting.domain.location;

import java.util.Map;

/**
 * The cost between 2 locations was precalculated on a real road network route.
 * The cost itself might be the distance in km, the travel time, the fuel usage
 * or a weighted function of any of those. Used with
 * {@link DistanceType#ROAD_DISTANCE}.
 */

public class RoadLocation extends Location {

    // Prefer Map over array or List because customers might be added and removed in
    // real-time planning.
    protected Map<RoadLocation, Double> travelDistanceMap;
    protected Double distanceStartToEnd = null;

    public RoadLocation() {
    }

    public Map<RoadLocation, Double> getTravelDistanceMap() {
        return travelDistanceMap;
    }

    public void setTravelDistanceMap(Map<RoadLocation, Double> travelDistanceMap) {
        this.travelDistanceMap = travelDistanceMap;
    }

    public Double getDistanceStartToEnd() {
        return distanceStartToEnd;
    }

    public void setDistanceStartToEnd(Double distanceStartToEnd) {
        this.distanceStartToEnd = distanceStartToEnd;
    }

    @Override
    public long getDistanceTo(Location location) {
        if (this == location) {
            return 0L;
        }

        Double distance = distanceStartToEnd + (Double) travelDistanceMap.get((RoadLocation) location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

}
