/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */

package org.evosuite.testcase.statements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.ConstantPoolManager;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.execution.CodeUnderTestException;
import org.evosuite.testcase.execution.Scope;
import org.evosuite.utils.Randomness;

/**
 * <p>
 * StringPrimitiveStatement class.
 * </p>
 * 
 * @author fraser
 */
public class StringPrimitiveStatement extends PrimitiveStatement<String> {

	private static final long serialVersionUID = 274445526699835887L;

	/**
	 * <p>
	 * Constructor for StringPrimitiveStatement.
	 * </p>
	 * 
	 * @param tc
	 *            a {@link org.evosuite.testcase.TestCase} object.
	 * @param value
	 *            a {@link java.lang.String} object.
	 */
	public StringPrimitiveStatement(TestCase tc, String value) {
		super(tc, String.class, value);
	}

	/**
	 * <p>
	 * Constructor for StringPrimitiveStatement.
	 * </p>
	 * 
	 * @param tc
	 *            a {@link org.evosuite.testcase.TestCase} object.
	 */
	public StringPrimitiveStatement(TestCase tc) {
		super(tc, String.class, "");
	}

	/* (non-Javadoc)
	 * @see org.evosuite.testcase.PrimitiveStatement#zero()
	 */
	/** {@inheritDoc} */
	@Override
	public void zero() {
		value = "";
	}

	private static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1);
	}

	private static String replaceCharAt(String s, int pos, char c) {
		return s.substring(0, pos) + c + s.substring(pos + 1);
	}

	private static String insertCharAt(String s, int pos, char c) {
		return s.substring(0, pos) + c + s.substring(pos);
	}

	private String StringInsert(String s, int pos) {
		final double ALPHA = 0.5;
		int count = 1;

		while (Randomness.nextDouble() <= Math.pow(ALPHA, count)
		        && s.length() < Properties.STRING_LENGTH) {
			count++;
			// logger.info("Before insert: '"+s+"'");
			s = insertCharAt(s, pos, Randomness.nextChar());
			// logger.info("After insert: '"+s+"'");
		}
		return s;
	}

	/** {@inheritDoc} */
	@Override
	public void delta() {

		String s = value;
		if(s == null) {
			randomize();
			return;
		}
		
		final double P2 = 1d / 3d;
		double P = 1d / s.length();
		// Delete
		if (Randomness.nextDouble() < P2) {
			for (int i = s.length(); i > 0; i--) {
				if (Randomness.nextDouble() < P) {
					// logger.info("Before remove at "+i+": '"+s+"'");
					s = removeCharAt(s, i - 1);
					// logger.info("After remove: '"+s+"'");
				}
			}
		}
		P = 1d / s.length();
		// Change
		if (Randomness.nextDouble() < P2) {
			for (int i = 0; i < s.length(); i++) {
				if (Randomness.nextDouble() < P) {
					// logger.info("Before change: '"+s+"'");
					s = replaceCharAt(s, i, Randomness.nextChar());
					// logger.info("After change: '"+s+"'");
				}
			}
		}

		// Insert
		if (Randomness.nextDouble() < P2) {
			// for(int i = 0; i < s.length(); i++) {
			// if(Randomness.nextDouble() < P) {
			int pos = 0;
			if (s.length() > 0)
				pos = Randomness.nextInt(s.length());
			s = StringInsert(s, pos);
			// }
			// }
		}
		value = s;
		// logger.info("Mutated string now is: "+value);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.testcase.PrimitiveStatement#increment(java.lang.Object)
	 */
	/**
	 * <p>
	 * increment
	 * </p>
	 */
	public void increment() {
		String s = value;
		if(s == null) {
			randomize();
			return;
		}
		else if (s.isEmpty()) {
			s += Randomness.nextChar();
		} else {
			s = replaceCharAt(s, Randomness.nextInt(s.length()), Randomness.nextChar());
		}

		value = s;
	}

	/* (non-Javadoc)
	 * @see org.evosuite.testcase.PrimitiveStatement#randomize()
	 */
	/** {@inheritDoc} */
	@Override
	public void randomize() {
		List<String> lines = new ArrayList<>();
        Path path = Paths.get("in.txt"); // read in the input file

        try { // handle exceptions
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("Error reading in.txt: " + e.getMessage());
            // fallback to constant pool or random string
            if (p >= Properties.PRIMITIVE_POOL) // generates a random string
                value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
            else { // pulls from a constant pool
                ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
                String candidateString = constantPool.getRandomString();
                if(Properties.MAX_STRING > 0 && candidateString.length() < Properties.MAX_STRING)
                    value = candidateString;
                else // generates a random string
                    value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
            }
            return;
        }

        if (lines.isEmpty()) {
            // fallback to constant pool or random string
            if (p >= Properties.PRIMITIVE_POOL) // generates a random string
                value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
            else { // pulls from a constant pool
                ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
                String candidateString = constantPool.getRandomString();
                if(Properties.MAX_STRING > 0 && candidateString.length() < Properties.MAX_STRING)
                    value = candidateString;
                else // generates a random string
                    value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
            }
        } else { 
            // calculate the weights based on the input file
            String line = "";
            int date = 0;
            int hex = 0;
            int unicode = 0;
            int negative = 0;
            int underscore = 0;
            int decimal = 0;
			int randString = 0;
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                String[] parts = str.split(": ");
                String pool = parts[1];
                if (pool.equals(DatePool)) {
                    date++;
                } else if (pool.equals(HexPool)) {
                    hex++;
                } else if (pool.equals(NegNumPool)) {
                    negative++;
                } else if (pool.equals(UnicodePool)) {
                    unicode++;
                } else if (pool.equals(DecimalPool)) {
                    decimal++;
                } else if (pool.equals(UnderscorePool)) {
					underscore++;
				} else if (pool.equals(StringPool)) {
					randString++;
				}
            }

			double probDate = date/7;
			double probHex = hex/7;
			double probUni = unicode/7;
			double probNeg = negative/7;
			double probUnder = underscore/7;
			double probDec = decimal/7;
			double probRandString = randString/7;

			double dateRange = probDate;
			double hexRange = dateRange + probHex;
			double uniRange = hexRange + probUni;
			double negRange = uniRange + probNeg;
			double underRange = negRange + probUnder;
			double decRange = underRange + probDec;

			CustomPools customPool = new CustomPools();
			double p = Randomness.nextDouble();
			if (p <= dateRange) {
				value = customPool.randomDate();
			} else if (p <= hexRange) {
				value = customPool.randomHex();
			} else if (p <= uniRange) {
				value = customPool.randomUnicode();
			} else if (p <= negRange) {
				value = customPool.randomNegativeNumber();
			} else if (p <= underRange) {
				value = customPool.randomUnderscoreString();
			} else if (p <= decRange) {
				value = customPool.randomLongDecimal();
			} else {
				if (p >= Properties.PRIMITIVE_POOL) // generates a random string
					value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
				else { // pulls from a constant pool
					ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
					String candidateString = constantPool.getRandomString();
					if(Properties.MAX_STRING > 0 && candidateString.length() < Properties.MAX_STRING)
						value = candidateString;
					else // generates a random string
						value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
				}
			}

		}

            
		/*double p = Randomness.nextDouble();

		if (p <= 0.5) {
			System.out.println("hex\n");
			value = "80000000";
		} else if (p <= 0.6) {
			System.out.println("date\n");
			value = "2005-05-13aa"; //maybe change later
		} else if (p <= 0.7) {
			System.out.println("unicode\n");
			value = "\uD83D\uDE30";
		} else if (p <= 0.8) {
			System.out.println("neg\n");
			value = "-1";
		} else {
			System.out.println("other\n");
			if (p >= Properties.PRIMITIVE_POOL)
				value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
			else {
				ConstantPool constantPool = ConstantPoolManager.getInstance().getConstantPool();
				String candidateString = constantPool.getRandomString();
				if(Properties.MAX_STRING > 0 && candidateString.length() < Properties.MAX_STRING)
					value = candidateString;
				else
					value = Randomness.nextString(Randomness.nextInt(Properties.STRING_LENGTH));
			}
		}*/
	}

	@Override
	public Throwable execute(Scope scope, PrintStream out)
	        throws InvocationTargetException, IllegalArgumentException,
	        IllegalAccessException, InstantiationException {
		Throwable exceptionThrown = null;

		try {
			if(value == null)
				retval.setObject(scope, null);
			else {
				// String literals may not be longer than 32767
				if(value.length() >= 32767)
					throw new CodeUnderTestException(new IllegalArgumentException("Maximum string length exceeded"));

				// In the JUnit code we produce, strings are generated as
				// String foo = "bar";
				// That means any reference comparison will behave different
				// as internally value is created as String foo = new String("bar").
				// Therefore we have to use the string object in the constant pool
				retval.setObject(scope, value.intern());
			}
		} catch (CodeUnderTestException e) {
			exceptionThrown = e;
		}
		return exceptionThrown;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();		
		oos.writeObject(value);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException,
	        IOException {
		ois.defaultReadObject();
		value = (String) ois.readObject();
	}
}
