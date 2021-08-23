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

package org.acme.vehiclerouting.domain.solver;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.Depot;
import org.acme.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleRideDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, Ride> {

    @Override
    public DepotAngleRideDifficultyWeight createSorterWeight(VehicleRoutingSolution vehicleRoutingSolution,
            Ride ride) {
        Depot depot = vehicleRoutingSolution.getDepotList().get(0);
        return new DepotAngleRideDifficultyWeight(ride,
                ride.getLocation().getAngle(depot.getLocation()),
                ride.getLocation().getDistanceTo(depot.getLocation())
                        + depot.getLocation().getDistanceTo(ride.getLocation()));
    }

    public static class DepotAngleRideDifficultyWeight
            implements Comparable<DepotAngleRideDifficultyWeight> {

        private static final Comparator<DepotAngleRideDifficultyWeight> COMPARATOR = comparingDouble(
                (DepotAngleRideDifficultyWeight weight) -> weight.depotAngle)
                        .thenComparingLong(weight -> weight.depotRoundTripDistance) // Ascending (further from the depot are more difficult)
                        .thenComparing(weight -> weight.ride, comparingLong(Ride::getId));

        private final Ride ride;
        private final double depotAngle;
        private final long depotRoundTripDistance;

        public DepotAngleRideDifficultyWeight(Ride ride,
                double depotAngle, long depotRoundTripDistance) {
            this.ride = ride;
            this.depotAngle = depotAngle;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        @Override
        public int compareTo(DepotAngleRideDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
