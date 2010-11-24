/*
 Copyright (c) 2010, The Staccato-Commons Team   
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */
package net.sf.staccato.commons.lang.check;

import static net.sf.staccato.commons.lang.Compare.between;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.staccato.commons.lang.collection.ContainsAware;
import net.sf.staccato.commons.lang.collection.EmptyAware;
import net.sf.staccato.commons.lang.collection.SizeAware;
import net.sf.staccato.commons.lang.format.Var;

/**
 * 
 * A {@link Check} is an object designed to validate conditions. It is heavily
 * inspired on several validation utilities like
 * {@link org.apache.commons.lang.Validate} from Apache commons, but is designed
 * to be more flexible and easy-to-use, so, it should be seen as higher lever
 * replacement.
 * 
 * The four main differences are the following
 * <ul>
 * <li>
 * This class is abstract and offers instance methods, and the exact behavior
 * when checks fail depends on subclasses. Three concrete implementation of
 * {@link Check}, are {@link Ensure}, {@link Assert} and {@link Validate}</li>
 * <li> {@link Check} offers a rich interface. Not only method for checking nulls
 * or boolean values, but also, {@link Collection}s sizes, regular expresion and
 * so on are exposed</li>
 * <li>Simple messages are automatically generated, so most user code do not
 * need to provide strings that tell what it is checking</li>
 * <li>Exception type thrown by implementors is generic, so they can throw
 * either checked or unchecked exceptions at no cost for client code</li>
 * </ul>
 * 
 * All check methods take a paremeterName as first argument. This is a String
 * identifier of the var being check, that allows to find it in context. For
 * example, if what is being check is a method var or local var, its var name
 * should be used, if is its an an attribute or property is being used, its
 * property name should be used.
 * 
 * 
 * @author flbulgarelli
 * 
 * @see Validate
 * @see Ensure
 * @see Assert
 * 
 * @param <ExceptionType>
 *          The kind of exception that this check will thrown on validation
 *          failures
 * 
 */
public abstract class Check<ExceptionType extends Throwable> {

	/* Minimal Ops */

	// private final Lazy<Check<ExceptionType>> not = new
	// Lazy<Check<ExceptionType>>() {
	// protected Check<ExceptionType> init() {
	// return new Not<ExceptionType>(Check.this);
	// }
	// };

	protected ExceptionType createVarException(VarFailure failure) {
		return createException(failure);
	}

	protected abstract ExceptionType createException(Failure failure);

	/**
	 * Fails, throwing an exception with a message. This method never returns
	 * normally.
	 * 
	 * @param <Nothing>
	 * @param message
	 *          the error message
	 * @param args
	 *          the error message args
	 * @return this method does never return normally
	 * @throws ExceptionType
	 *           always
	 */
	public <Nothing> Nothing fail(String message, Object... args) throws ExceptionType {
		throw createException(new Failure(message, args));
	}

	/**
	 * Fails, throwing an exception with a message, the var and its name. This
	 * method never returns normally.
	 * 
	 * @param <Nothing>
	 * @param varName
	 *          the name of the variable whose check failed
	 * @param var
	 *          the variable whose check failed
	 * @param message
	 *          the error message
	 * @param args
	 *          the error message arguments
	 * @return this method does never return normally
	 * @throws ExceptionType
	 *           always
	 */
	public <Nothing> Nothing fail(String varName, Object var, String message, Object... args)
		throws ExceptionType {
		throw createVarException(new VarFailure(varName, var, message, args));
	}

	/**
	 * Checks a condition, failing if not met.
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param condition
	 *          the condition to be checked
	 * @param message
	 *          the error message
	 * @param args
	 *          the error message arguments
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public Check<ExceptionType> is(String varName, Object var, boolean condition, String message,
		Object... args) throws ExceptionType {
		if (!condition)
			fail(varName, var, message, args);
		return this;
	}

	/**
	 * Checks a that a condition is true, failing if it is not.
	 * 
	 * @param condition
	 *          the condition to be checked
	 * @param message
	 *          the error message
	 * @param args
	 *          the error message arguments
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public Check<ExceptionType> is(boolean condition, String message, Object... args)
		throws ExceptionType {
		if (!condition)
			fail(message, args);
		return this;
	}

	/**
	 * Checks the variable is not null.
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNull(String varName, Object var) throws ExceptionType {
		if (var == null)
			fail(varName, var, "must not be null");
		return this;
	}

	/**
	 * Checks the variable is null.
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNull(String varName, Object var) throws ExceptionType {
		if (var != null)
			fail(varName, var, "must be null");
		return this;
	}

	/* Extra ops */

	/**
	 * Checks a that the variable is true, failing with a generated message if it
	 * is not.
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the var to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isTrue(String varName, boolean var) throws ExceptionType {
		return is(varName, var, var, "must be true");
	}

	/**
	 * Checks that the variable is not null and matches a given regular
	 * expression.
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the var to be checked
	 * @param regex
	 *          the regular expression the var must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public final Check<ExceptionType> matches(String varName, String var, String regex)
		throws ExceptionType {
		return isNotNull(varName, var).matches(varName, var, Pattern.compile(regex));
	}

	/**
	 * Checks that the variable is not null and matches a given pattern
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param pattern
	 *          the pattern the variable must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public final Check<ExceptionType> matches(String varName, String var, Pattern pattern)
		throws ExceptionType {
		return isNotNull(varName, var) //
			.is(varName, var, pattern.matcher(var).matches(), "must match %s", pattern.pattern());
	}

	/**
	 * Checks that the variable is not null and empty
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param pattern
	 *          the pattern the variable must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public Check<ExceptionType> isEmpty(String varName, Collection<?> var) throws ExceptionType {
		return isNotNull(varName, var).isEmpty(varName, var, var.isEmpty());
	}

	/**
	 * Checks that the variable is not null and empty
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param pattern
	 *          the pattern the variable must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public Check<ExceptionType> isEmpty(String varName, Map<?, ?> var) throws ExceptionType {
		return isNotNull(varName, var).isEmpty(varName, var, var.isEmpty());
	}

	/**
	 * Checks that the variable is not null and empty
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param pattern
	 *          the pattern the variable must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public Check<ExceptionType> isEmpty(String varName, EmptyAware var) throws ExceptionType {
		return isNotNull(varName, var).isEmpty(varName, var, var.isEmpty());
	}

	private final Check<ExceptionType> isEmpty(String varName, Object var, boolean empty)
		throws ExceptionType {
		return is(varName, var, empty, "must be empty");
	}

	/**
	 * Checks that the variable is not null and instance of the given class
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @param expectedClass
	 *          the class the variable must be instance of
	 * @param pattern
	 *          the pattern the variable must match
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 * 
	 * @see #is(boolean, String, Object...)
	 */
	public Check<ExceptionType> isInstanceOf(String varName, Object var, Class<?> expectedClass)
		throws ExceptionType {
		return is(varName, var, expectedClass.isInstance(var),//
			"must be instance of class %s",
			expectedClass);
	}

	public Check<ExceptionType> isSize(String varName, Collection<?> var, int size)
		throws ExceptionType {
		return isSize(varName, var, size, var.size());
	}

	public Check<ExceptionType> isSize(String varName, CharSequence var, int size)
		throws ExceptionType {
		return isSize(varName, var, size, var.length());
	}

	public Check<ExceptionType> isSize(String varName, Object[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, int[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, long[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, byte[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, double[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, float[] var, int size) throws ExceptionType {
		return isSize(varName, var, size, var.length);
	}

	public Check<ExceptionType> isSize(String varName, SizeAware value, int size)
		throws ExceptionType {
		return isSize(varName, value, size, value.size());
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, long var) throws ExceptionType {
		return isNotNegative(varName, var, var >= 0);
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, int number) throws ExceptionType {
		return isNotNegative(varName, number, number >= 0);
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, double number)
		throws ExceptionType {
		return isNotNegative(varName, number, number >= 0);
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, float number)
		throws ExceptionType {
		return isNotNegative(varName, number, number >= 0);
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, BigDecimal number)
		throws ExceptionType {
		return isNotNull(varName, number) //
			.isNotNegative(varName, number, number.compareTo(BigDecimal.ZERO) >= 0);
	}

	/**
	 * Checks the var is &gt;= 0
	 * 
	 * @param varName
	 *          the name of the variable to be checked
	 * @param var
	 *          the variable to be checked
	 * @return this, in order to allow method chaining
	 * @throws ExceptionType
	 *           if the check failed
	 */
	public final Check<ExceptionType> isNotNegative(String varName, BigInteger number)
		throws ExceptionType {
		return isNotNull(varName, number) //
			.isNotNegative(varName, number, number.compareTo(BigInteger.ZERO) >= 0);
	}

	private Check<ExceptionType> isNotNegative(String varName, Object number, boolean negative)
		throws ExceptionType {
		return is(varName, number, negative, "Must be not negative");
	}

	public void isNotEmpty(String varName, EmptyAware var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.isEmpty())
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, Collection<?> var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.isEmpty())
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, Iterable<?> var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.iterator().hasNext())
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, Map<?, ?> var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.isEmpty())
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, CharSequence var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length() == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, Object[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, int[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, long[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, byte[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, double[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	public void isNotEmpty(String varName, float[] var) throws ExceptionType {
		isNotNull(varName, var);
		if (var.length == 0)
			isNotEmptyFailed(varName, var);
	}

	/*
	 * Performance de array.getLength *
	 */
	/*
	 * Performance del dispatch virtual
	 */
	/*
	 * Performance del not
	 */
	/*
	 * Performance del objeto TypeClass
	 */

	private void isNotEmptyFailed(String varName, Object var) throws ExceptionType {
		fail(varName, var, //
			"%s must not be empty", //
			Var.format(varName, var));
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, long var) throws ExceptionType {
		return isPositive(varName, var, var > 0);
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, int var) throws ExceptionType {
		return isPositive(varName, var, var > 0);
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, double var) throws ExceptionType {
		return isPositive(varName, var, var > 0);
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, float var) throws ExceptionType {
		return isPositive(varName, var, var > 0);
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 *          not null
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, BigDecimal var) throws ExceptionType {
		return isNotNull(varName, var).isPositive(varName, var, var.compareTo(BigDecimal.ZERO) > 0);
	}

	/**
	 * Checks the var is &lt;= 0
	 * 
	 * @param varName
	 * @param var
	 *          not null.
	 * @return
	 * @throws ExceptionType
	 */
	public Check<ExceptionType> isPositive(String varName, BigInteger var) throws ExceptionType {
		return isNotNull(varName, var).isPositive(varName, var, var.compareTo(BigInteger.ZERO) > 0);
	}

	private Check<ExceptionType> isPositive(String varName, Object var, boolean positive)
		throws ExceptionType {
		return is(varName, var, positive, "must be positive");
	}

	private final Check<ExceptionType> isSize(String varName, Object var, int expectedSize,
		int actualSize) throws ExceptionType {
		return is(varName, var, actualSize == expectedSize, //
			"must be of size %s, but was %s",
			expectedSize,
			actualSize);
	}

	public <T extends Comparable<T>> void isBetween(String varName, T var, T min, T max)
		throws ExceptionType {
		if (!between(var, min, max))
			fail(varName, var,//
				"%s must be between %s and %s",
				Var.format(varName, var),
				min,
				max);
	}

	public <T> Check<ExceptionType> contains(String varName, ContainsAware<T> var, T element)
		throws ExceptionType {
		return isNotNull(varName, var)//
			.is(varName, var, var.contains(element), "must contain %s", element);
	}

	public <T> Check<ExceptionType> isIn(String varName, T var, ContainsAware<T> container)
		throws ExceptionType {
		return isNotNull(varName, var)//
			.is(varName, var, container.contains(var), "must be in %s", container);
	}

	public <T extends Comparable<T>> void isGreatherThan(String varName, T var, T other)
		throws ExceptionType {
		if (!(var.compareTo(other) > 0))
			fail(varName, var,//
				"%s must be greather than %s",
				Var.format(varName, var),
				other);
	}

	public <T extends Comparable<T>> void isLowerThan(String varName, T var, T other)
		throws ExceptionType {
		if (!(var.compareTo(other) < 0))
			fail(varName, var,//
				"%s must be lower than %s",
				Var.format(varName, var),
				other);
	}

	// public Check<ExceptionType> not() {
	// return not.value();
	// }

	/**
	 * A basic check failure
	 * 
	 * @author flbulgarelli
	 */
	public static class Failure {
		private final String message;
		private final Object[] messageArgs;

		/**
		 * Creates a new {@link Failure}
		 * 
		 * @param message
		 *          the failure message
		 * @param messageArgs
		 *          the failure message arguments
		 */
		public Failure(String message, Object... messageArgs) {
			this.message = message;
			this.messageArgs = messageArgs;
		}

		/**
		 * @return the failure message
		 */
		public String createMessage() {
			return String.format(message, messageArgs);
		}
	}

	/**
	 * A check failure that contains the name and value of the variable that was
	 * checked
	 * 
	 * @author flbulgarelli
	 * 
	 */
	public static class VarFailure extends Failure {
		private final String varName;
		private final Object var;

		/**
		 * Creates a new {@link VarFailure}
		 * 
		 * @param varName
		 *          the name of the variable checked
		 * @param var
		 *          the variable checked
		 * 
		 * @param message
		 *          the failure message
		 * @param messageArgs
		 *          the failure message arguments
		 */
		public VarFailure(String varName, Object var, String message, Object... messageArgs) {
			super(message, messageArgs);
			this.varName = varName;
			this.var = var;
		}

		/**
		 * @return the var
		 */
		public Object getVar() {
			return var;
		}

		/**
		 * @return the varName
		 */
		public String getVarName() {
			return varName;
		}

		/**
		 * @return the failure message, which does not contain the variable name nor
		 *         value
		 */
		public String createSimpleMessage() {
			return super.createMessage();
		}

		/**
		 * @return the failure message, which contains the variable name and value
		 */
		public String createMessage() {
			return Var.format("Check failure: ", varName, var, super.createMessage());
		}
	}

	// private static final class Not<ExceptionType extends Throwable> extends
	// Check<ExceptionType> {
	//
	// private final Check<ExceptionType> check;
	//
	// public Not(Check<ExceptionType> check) {
	// this.check = check;
	// }
	//
	// protected ExceptionType createException(String message) {
	// return check.createException(message);
	// }
	//
	// protected ExceptionType createException(String varName, Object var, String
	// message) {
	// return check.createException(varName, var, message);
	// }
	//
	// public Check<ExceptionType> is(boolean condition, String message, Object...
	// args)
	// throws ExceptionType {
	// super.is(!condition, message, args);
	// return check;
	// }
	//
	// public Check<ExceptionType> is(String varName, Object var, boolean
	// condition, String message,
	// Object... args) throws ExceptionType {
	// super.is(varName, var, !condition, message, args);
	// return check;
	// }
	//
	// public Check<ExceptionType> not() {
	// return check;
	// }
	//
	// }

}
