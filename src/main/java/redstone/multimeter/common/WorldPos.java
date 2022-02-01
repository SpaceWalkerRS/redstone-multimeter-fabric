package redstone.multimeter.common;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

import redstone.multimeter.util.NbtUtils;

public class WorldPos {
	
	private final Identifier worldId;
	private final BlockPos blockPos;
	
	public WorldPos(Identifier worldId, BlockPos blockPos) {
		this.worldId = worldId;
		this.blockPos = blockPos.toImmutable();
	}
	
	public WorldPos(Identifier worldId, int x, int y, int z) {
		this(worldId, new BlockPos(x, y, z));
	}
	
	public WorldPos(World world, BlockPos pos) {
		this(world.getRegistryKey().getValue(), pos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldPos) {
			WorldPos pos = (WorldPos)obj;
			return pos.worldId.equals(worldId) && pos.blockPos.equals(blockPos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return blockPos.hashCode() + 31 * worldId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", worldId.toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public Identifier getWorldId() {
		return worldId;
	}
	
	public boolean isOf(World world) {
		return world.getRegistryKey().getValue().equals(worldId);
	}
	
	public WorldPos offset(Identifier worldId) {
		return new WorldPos(worldId, blockPos);
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public WorldPos offset(Direction dir) {
		return offset(dir, 1);
	}
	
	public WorldPos offset(Direction dir, int distance) {
		return new WorldPos(worldId, blockPos.offset(dir, distance));
	}
	
	public WorldPos offset(Axis axis) {
		return offset(axis, 1);
	}
	
	public WorldPos offset(Axis axis, int distance) {
		return new WorldPos(worldId, blockPos.offset(axis, distance));
	}
	
	public WorldPos offset(int dx, int dy, int dz) {
		return new WorldPos(worldId, blockPos.add(dx, dy, dz));
	}
	
	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		
		nbt.put("world id", NbtUtils.identifierToNbt(worldId));
		nbt.putInt("x", blockPos.getX());
		nbt.putInt("y", blockPos.getY());
		nbt.putInt("z", blockPos.getZ());
		
		return nbt;
	}
	
	public static WorldPos fromNbt(NbtCompound nbt) {
		Identifier worldId = NbtUtils.nbtToIdentifier(nbt.getCompound("world id"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");
		
		return new WorldPos(worldId, x, y, z);
	}
}
