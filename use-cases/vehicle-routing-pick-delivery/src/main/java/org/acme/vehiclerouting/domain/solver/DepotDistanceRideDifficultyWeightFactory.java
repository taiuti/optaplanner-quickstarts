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

import static java.util.Comparator.comparingLong;

import java.util.Comparator;

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.Depot;
import org.acme.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

/**
 * On large datasets, the constructed solution looks like a Matryoshka doll.
 */
public class DepotDistanceRideDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, Ride> {

    @Override
    public DepotDistanceRideDifficultyWeight createSorterWeight(VehicleRoutingSolution vehicleRoutingSolution,
            Ride ride) {
        Depot depot = vehicleRoutingSolution.getDepotList().get(0);
        return new DepotDistanceRideDifficultyWeight(ride,
                ride.getLocation().getDistanceTo(depot.getLocation())
                        + depot.getLocation().getDistanceTo(ride.getLocation()));
    }

    public static class DepotDistanceRideDifficultyWeight
            implements Comparable<DepotDistanceRideDifficultyWeight> {

        private static final Comparator<DepotDistanceRideDifficultyWeight> COMPARATOR =
                // Ascending (further from the depot are more difficult)
                comparingLong((DepotDistanceRideDifficultyWeight weight) -> weight.depotRoundTripDistance)
                        .thenComparingInt(weight -> weight.ride.getDemand())
                        .thenComparingDouble(weight -> weight.ride.getLocation().getPickup().getLatitude())
                        .thenComparingDouble(weight -> weight.ride.getLocation().getPickup().getLongitude())
                        .thenComparingDouble(weight -> weight.ride.getLocation().getDelivery().getLatitude())
                        .thenComparingDouble(weight -> weight.ride.getLocation().getDelivery().getLongitude())
                        .thenComparing(weight -> weight.ride, comparingLong(Ride::getId));

        private final Ride ride;
        private final long depotRoundTripDistance;

        public DepotDistanceRideDifficultyWeight(Ride ride, long depotRoundTripDistance) {
            this.ride = ride;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        @Override
        public int compareTo(DepotDistanceRideDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
