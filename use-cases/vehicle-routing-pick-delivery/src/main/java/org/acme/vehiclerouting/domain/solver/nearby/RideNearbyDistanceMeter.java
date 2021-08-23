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

package org.acme.vehiclerouting.domain.solver.nearby;

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.Standstill;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

public class RideNearbyDistanceMeter implements NearbyDistanceMeter<Ride, Standstill> {

    @Override
    public double getNearbyDistance(Ride origin, Standstill destination) {
        long distance = origin.getDistanceTo(destination);
        // If arriving early also inflicts a cost (more than just not using the vehicle
        // more), such as the driver's wage, use this:
        // if (origin instanceof TimeWindowedRide && destination instanceof
        // TimeWindowedRide) {
        // distance += ((TimeWindowedRide)
        // origin).getTimeWindowGapTo((TimeWindowedRide) destination);
        // }
        return distance;
    }

}
