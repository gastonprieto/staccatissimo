/*
 Copyright (c) 2011, The Staccato-Commons Team

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */
package net.sf.staccatocommons.control.monad;

import static net.sf.staccatocommons.control.monad.Monads.*;
import static net.sf.staccatocommons.lang.number.NumberTypes.*;

import java.util.concurrent.Executors;

import net.sf.staccatocommons.defs.Applicable;
import net.sf.staccatocommons.defs.Executable;
import net.sf.staccatocommons.io.IO;
import net.sf.staccatocommons.lang.Compare;

import org.junit.Test;

/**
 * @author flbulgarelli
 * 
 */
public class MonadTest {

  @Test
  public void testname() throws Exception {
    Monads//
      .cons(4)
      .map(add(5))
      .map(add(1))
      .filter(Compare.greaterThan(2))
      .each(IO.printlnSysout())
      .value();
  }

  public void testKleisli() throws Exception {
    Monads //
      .from(4, 5, 6)
      .bind(map(add(5)) //
        .then(map(add(1)))
        .then(map(add(90)))
        .then(filter(Compare.greaterThan(2)))
        .then(each(IO.printlnSysout())))
      .value();
  }

  @Test
  public void testAsync() throws Exception {
    Monads //
      .cons(4)
      .map(logThread())
      .map(add(1))
      .fork(Executors.newSingleThreadExecutor())
      .map(logThread())
      .value();
    Thread.sleep(1000);
  }

  @Test
  public void testIterable() throws Exception {
    Monads //
      .from(4, 5, 6, 9)
      .map(add(1))
      .filter(Compare.greaterThan(6))
      .each(IO.printlnSysout())
      .value();

  }

  protected Applicable<Integer, Integer> logThread() {
    return new Applicable<Integer, Integer>() {
      public Integer apply(Integer arg) {
        System.out.println(Thread.currentThread());
        return arg;
      }
    };
  }

  public static <A> Executable<A> printStackTrace() {
    return new Executable<A>() {
      public void exec(A argument) {
        try {
          throw new Exception();
        } catch (Exception e) {
          e.printStackTrace(System.out);
        }
      }
    };

  }
}
