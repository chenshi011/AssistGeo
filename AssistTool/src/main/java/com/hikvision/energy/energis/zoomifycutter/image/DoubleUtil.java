package com.hikvision.energy.energis.zoomifycutter.image;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 双精度运算工具类，提供相对精确的计算
 *
 */
public class DoubleUtil implements Serializable {

	private static final long serialVersionUID = -3345205828566485102L;
	// 默认除法运算精度
	private static final Integer DEF_DIV_SCALE = 13;

	/**
	 * 精确的加法运算。
	 * 
	 * @param value1
	 *            被加数
	 * @param value2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(Number value1, Number value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
		BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 精确的减法运算。
	 * 
	 * @param value1
	 *            被减数
	 * @param value2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(Number value1, Number value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
		BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 精确的乘法运算。
	 * 
	 * @param value1
	 *            被乘数
	 * @param value2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static Double mul(Number value1, Number value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
		BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * （相对）精确的幂运算，结果为<tt>(base<sup>index</sup>)</tt>
	 * <p>
	 * 当指数为正整数时可得到精确值
	 * 
	 * @param base
	 *            底数
	 * @param index
	 *            指数
	 * @return
	 */
	public static double pow(Number base, int index) {
		BigDecimal b1 = new BigDecimal(Double.toString(base.doubleValue()));
		if (index >= 0) {
			return b1.pow(index).doubleValue();
		} else {
			b1 = b1.pow(-index);
			return new BigDecimal(1).divide(b1, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}

	/**
	 * （相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后13位，以后的数字四舍五入。
	 * 
	 * @param dividend
	 *            被除数
	 * @param divisor
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double dividend, double divisor) {
		return div(dividend, divisor, DEF_DIV_SCALE);
	}

	/**
	 * （相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 * 
	 * @param dividend
	 *            被除数
	 * @param divisor
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double dividend, double divisor, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(dividend));
		BigDecimal b2 = new BigDecimal(Double.toString(divisor));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 精确的小数位四舍五入处理。
	 * 
	 * @param value
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(value));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 默认位数的精确的小数位四舍五入处理。
	 * 
	 * @param value
	 *            需要四舍五入的数字
	 * @return 四舍五入后的结果
	 */
	public static double round(double value) {
		return round(value, DEF_DIV_SCALE);
	}
}
