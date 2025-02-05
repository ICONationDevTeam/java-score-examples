/*
 * Copyright 2020 ICONLOOP Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iconloop.score.example;

import com.iconloop.testsvc.Account;
import com.iconloop.testsvc.Score;
import com.iconloop.testsvc.ServiceManager;
import com.iconloop.testsvc.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static java.math.BigInteger.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTokenTest extends TestBase {
    private static final String name = "MySampleToken";
    private static final String symbol = "MST";
    private static final int decimals = 18;
    private static final BigInteger initialSupply = BigInteger.valueOf(1000);

    private static final BigInteger totalSupply = initialSupply.multiply(TEN.pow(decimals));
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();

    private static Score tokenScore;

    @BeforeAll
    public static void setup() throws Exception {
        tokenScore = sm.deploy(owner, SampleToken.class,
                name, symbol, BigInteger.valueOf(decimals), initialSupply);
        owner.addBalance(symbol, totalSupply);
    }

    @Test
    void name() {
        assertEquals(name, tokenScore.call("name"));
    }

    @Test
    void symbol() {
        assertEquals(symbol, tokenScore.call("symbol"));
    }

    @Test
    void decimals() {
        assertEquals(decimals, tokenScore.call("decimals"));
    }

    @Test
    void totalSupply() {
        assertEquals(totalSupply, tokenScore.call("totalSupply"));
    }

    @Test
    void balanceOf() {
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
    }

    @Test
    void transfer() {
        Account alice = sm.createAccount();
        BigInteger value = TEN.pow(decimals);
        tokenScore.invoke(owner, "transfer", alice.getAddress(), value, "to alice".getBytes());
        owner.subtractBalance(symbol, value);
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
        assertEquals(value,
                tokenScore.call("balanceOf", alice.getAddress()));

        // transfer self
        tokenScore.invoke(alice, "transfer", alice.getAddress(), value, "self transfer".getBytes());
        assertEquals(value, tokenScore.call("balanceOf", alice.getAddress()));
    }
}
