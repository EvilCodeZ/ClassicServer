package de.evilcodez.classicserver.utils;

public class AxisAlignedBB {

	public final double minX;
	public final double minY;
	public final double minZ;
	public final double maxX;
	public final double maxY;
	public final double maxZ;

	public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public AxisAlignedBB setMaxY(double y2) {
		return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof AxisAlignedBB)) {
			return false;
		} else {
			AxisAlignedBB axisalignedbb = (AxisAlignedBB) other;

			if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
				return false;
			} else {
				return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
			}
		}
	}

	public int hashCode() {
		long i = Double.doubleToLongBits(this.minX);
		int j = (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.minY);
		j = 31 * j + (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.minZ);
		j = 31 * j + (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.maxX);
		j = 31 * j + (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.maxY);
		j = 31 * j + (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.maxZ);
		j = 31 * j + (int) (i ^ i >>> 32);
		return j;
	}

	public AxisAlignedBB addCoord(double x, double y, double z) {
		double d0 = this.minX;
		double d1 = this.minY;
		double d2 = this.minZ;
		double d3 = this.maxX;
		double d4 = this.maxY;
		double d5 = this.maxZ;

		if (x < 0.0D) {
			d0 += x;
		} else if (x > 0.0D) {
			d3 += x;
		}

		if (y < 0.0D) {
			d1 += y;
		} else if (y > 0.0D) {
			d4 += y;
		}

		if (z < 0.0D) {
			d2 += z;
		} else if (z > 0.0D) {
			d5 += z;
		}

		return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
	}

	public AxisAlignedBB expand(double x, double y, double z) {
		double d0 = this.minX - x;
		double d1 = this.minY - y;
		double d2 = this.minZ - z;
		double d3 = this.maxX + x;
		double d4 = this.maxY + y;
		double d5 = this.maxZ + z;
		return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
	}

	public AxisAlignedBB expandXyz(double value) {
		return this.expand(value, value, value);
	}

	public AxisAlignedBB offset(double x, double y, double z) {
		return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y,
				this.maxZ + z);
	}

	public double calculateXOffset(AxisAlignedBB other, double offsetX) {
		if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
			if (offsetX > 0.0D && other.maxX <= this.minX) {
				double d1 = this.minX - other.maxX;

				if (d1 < offsetX) {
					offsetX = d1;
				}
			} else if (offsetX < 0.0D && other.minX >= this.maxX) {
				double d0 = this.maxX - other.minX;

				if (d0 > offsetX) {
					offsetX = d0;
				}
			}

			return offsetX;
		} else {
			return offsetX;
		}
	}

	public double calculateYOffset(AxisAlignedBB other, double offsetY) {
		if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
			if (offsetY > 0.0D && other.maxY <= this.minY) { // Above block check
				double d1 = this.minY - other.maxY;

				if (d1 < offsetY) {
					offsetY = d1;
				}
			} else if (offsetY < 0.0D && other.minY >= this.maxY) { // Below block check
				double d0 = this.maxY - other.minY;

				if (d0 > offsetY) {
					offsetY = d0;
				}
			}

			return offsetY;
		} else {
			return offsetY;
		}
	}

	public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
		if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
			if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
				double d1 = this.minZ - other.maxZ;

				if (d1 < offsetZ) {
					offsetZ = d1;
				}
			} else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
				double d0 = this.maxZ - other.minZ;

				if (d0 > offsetZ) {
					offsetZ = d0;
				}
			}

			return offsetZ;
		} else {
			return offsetZ;
		}
	}

	public boolean intersectsWith(AxisAlignedBB other) {
		return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
	}

	public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
		return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
	}

	public double getAverageEdgeLength() {
		double d0 = this.maxX - this.minX;
		double d1 = this.maxY - this.minY;
		double d2 = this.maxZ - this.minZ;
		return (d0 + d1 + d2) / 3.0D;
	}

	public AxisAlignedBB contract(double value) {
		return this.expandXyz(-value);
	}

	public String toString() {
		return "AxisAlignedBB[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", "
				+ this.maxZ + "]";
	}

	public boolean hasNaN() {
		return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX)
				|| Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
	}
}
