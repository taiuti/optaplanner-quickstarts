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

package org.acme.vehiclerouting.domain.location.segmented;

import static org.assertj.core.api.Assertions.assertThat;

import org.acme.vehiclerouting.domain.location.AirLocation;
import org.acme.vehiclerouting.domain.location.Point;
import org.junit.jupiter.api.Test;

public class AirLocationTest {

    @Test
    public void getDistance() {

        long id = 0;

        // distance single point a -> b
        AirLocation a = new AirLocation(id++, "", new Point(id++, "", 0.0, 0.0));
        AirLocation b = new AirLocation(id++, "", new Point(id++, "", 4.0, 3.0));

        long distance = a.getDistanceTo(b);
        assertThat(distance).isEqualTo(5000);

        // distance point to segment
        a = new AirLocation(id++, "", new Point(id++, "", 0.0, 0.0));
        b = new AirLocation(id++, "", new Point(id++, "", 3.0, 0.0), new Point(id++, "", 3.0, 4.0));

        distance = a.getDistanceTo(b);
        assertThat(distance).isEqualTo(7000);

        // distance segment to segment
        a = new AirLocation(id++, "", new Point(id++, "", 0.0, 0.0), new Point(id++, "", 3.0, 0.0));
        b = new AirLocation(id++, "", new Point(id++, "", 3.0, 2.0), new Point(id++, "", 3.0, 4.0));

        distance = a.getDistanceTo(b);
        assertThat(distance).isEqualTo(7000);

        distance = b.getDistanceTo(a);
        assertThat(distance).isEqualTo(10000);

    }

}
