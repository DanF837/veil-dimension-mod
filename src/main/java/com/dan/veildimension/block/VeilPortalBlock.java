package com.dan.veildimension.block;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.ModDimensions;
import com.dan.veildimension.ModItems;
import com.dan.veildimension.util.SupplyCacheSpawner;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import com.dan.veildimension.util.InventoryManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class VeilPortalBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public VeilPortalBlock()
    {
        super(Settings.create()
                .mapColor(MapColor.PURPLE)
                .noCollision()
                .strength(-1.0F)
                .sounds(BlockSoundGroup.GLASS)
                .luminance(state -> 11));
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return state.get(AXIS) == Direction.Axis.Z ? Z_SHAPE : X_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        // If a frame block was removed nearby, check if portal is still valid
        if (neighborState.isOf(Blocks.AIR) || (!neighborState.isOf(ModBlocks.VEIL_PORTAL_FRAME) && !neighborState.isOf(ModBlocks.VEIL_PORTAL)))
        {
            // Check if we still have a complete frame
            if (!isPortalValid(world, pos))
            {
                // Break ALL connected portal blocks
                destroyEntirePortal(world, pos);
                return Blocks.AIR.getDefaultState();
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean isPortalValid(WorldAccess world, BlockPos pos)
    {
        // Check if there's at least one frame block adjacent
        for (Direction dir : Direction.values())
        {
            if (world.getBlockState(pos.offset(dir)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                return true;
            }
        }
        return false;
    }

    private void destroyEntirePortal(WorldAccess world, BlockPos startPos)
    {
        // Use flood fill to find and destroy all connected portal blocks
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.add(startPos);
        visited.add(startPos);

        while (!toCheck.isEmpty())
        {
            BlockPos current = toCheck.poll();

            // Check all 6 directions for more portal blocks
            for (Direction dir : Direction.values())
            {
                BlockPos neighbor = current.offset(dir);

                if (!visited.contains(neighbor) && world.getBlockState(neighbor).isOf(ModBlocks.VEIL_PORTAL))
                {
                    visited.add(neighbor);
                    toCheck.add(neighbor);
                    // Destroy this portal block
                    world.setBlockState(neighbor, Blocks.AIR.getDefaultState(), 3);
                }
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            if (entity.canUsePortals(false) && !entity.hasVehicle() && !entity.hasPassengers())
            {
                // Get the destination dimension
                RegistryKey<World> destinationKey = world.getRegistryKey() == ModDimensions.VEIL_WORLD ? World.OVERWORLD : ModDimensions.VEIL_WORLD;

                ServerWorld destinationWorld = serverWorld.getServer().getWorld(destinationKey);

                if (destinationWorld != null)
                {
                    // Calculate safe spawn position
                    BlockPos destinationPos;

                    if (destinationKey == ModDimensions.VEIL_WORLD)
                    {
                        // Entering Veil - find surface level from TOP down
                        destinationPos = findSafeSpawnPosition(destinationWorld, entity.getBlockPos());
                    }
                    else
                    {
                        // Returning to Overworld - use world spawn
                        destinationPos = destinationWorld.getSpawnPos();
                    }

                    // If entering Veil Dimension and is a player, save inventory and give return scroll
                    if (destinationKey == ModDimensions.VEIL_WORLD && entity instanceof ServerPlayerEntity serverPlayer)
                    {
                        // Check if this is first time BEFORE saving inventory
                        boolean isFirstTime = !InventoryManager.hasVeilInventory(serverPlayer);

                        // Save and clear inventory BEFORE teleporting (this also loads Veil inventory)
                        InventoryManager.saveAndClearInventory(serverPlayer);

                        // Spawn supply cache near first-time arrivals AFTER teleport
                        if (isFirstTime)
                        {
                            BlockPos finalDestinationPos = destinationPos;
                            serverPlayer.getServer().execute(() -> {
                                SupplyCacheSpawner.spawnSupplyCacheNearPlayer(serverPlayer, destinationWorld);

                                // Also generate lantern trail
                                com.dan.veildimension.world.ModStructures.generateLanternTrailFromPlayer(serverPlayer, destinationWorld);
                            });
                        }

                        // Only give return scroll if player doesn't have one
                        boolean hasScroll = false;
                        for (int i = 0; i < serverPlayer.getInventory().size(); i++)
                        {
                            ItemStack stack = serverPlayer.getInventory().getStack(i);
                            if (stack.isOf(ModItems.VEIL_RETURN_SCROLL))
                            {
                                hasScroll = true;
                                break;
                            }
                        }
                        if (!hasScroll)
                        {
                            ItemStack scroll = new ItemStack(ModItems.VEIL_RETURN_SCROLL);
                            serverPlayer.giveItemStack(scroll);
                        }

                        serverPlayer.sendMessage(Text.literal("§5§oYour belongings fade as a new journey begins...§r"), true);
                    }

                    // If LEAVING Veil Dimension, restore inventory
                    if (destinationKey == World.OVERWORLD && entity instanceof ServerPlayerEntity serverPlayer)
                    {
                        InventoryManager.restoreInventory(serverPlayer);
                        serverPlayer.sendMessage(Text.literal("§5§oYour belongings return to you as the journey comes to an end...§r"), true);
                    }

                    // Create teleport target with safe position
                    Vec3d destination = new Vec3d(destinationPos.getX() + 0.5, destinationPos.getY(), destinationPos.getZ() + 0.5);

                    // Teleport
                    entity.teleportTo(new TeleportTarget(
                            destinationWorld,
                            destination,
                            entity.getVelocity(),
                            entity.getYaw(),
                            entity.getPitch(),
                            TeleportTarget.NO_OP
                    ));

                    // Play teleport sound
                    world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRAVEL,
                            SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        // Portal ambient sound (less frequent)
        if (random.nextInt(100) == 0)
        {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        // Determine which direction the portal is facing
        Direction.Axis axis = state.get(AXIS);

        // Spawn particles on both sides of the portal
        for (int i = 0; i < 2; i++)
        {
            double x = pos.getX();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ();

            if (axis == Direction.Axis.X)
            {
                // Portal faces East-West
                x += random.nextDouble();
                z += random.nextBoolean() ? 0.1 : 0.9; // Spawn near edges
            }
            else
            {
                // Portal faces North-South
                x += random.nextBoolean() ? 0.1 : 0.9; // Spawn near edges
                z += random.nextDouble();
            }

            // Velocity for particles
            double velocityX = (random.nextDouble() - 0.5) * 0.5;
            double velocityY = (random.nextDouble() - 0.5) * 0.5;
            double velocityZ = (random.nextDouble() - 0.5) * 0.5;

            // Main portal particles (swirling effect)
            world.addParticle(ParticleTypes.PORTAL, x, y, z, velocityX, velocityY, velocityZ);
        }

        // Add some soul particles for extra mystical effect (less frequent)
        if (random.nextInt(3) == 0)
        {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + 0.5;

            if (axis == Direction.Axis.X)
            {
                x += random.nextDouble() - 0.5;
            }
            else
            {
                z += random.nextDouble() - 0.5;
            }

            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.05, 0);
        }

        // Occasional sparkles
        if (random.nextInt(5) == 0)
        {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        // Dripping effect from top of portal (rare)
        if (random.nextInt(10) == 0 && pos.getY() < world.getTopY() - 1)
        {
            BlockState above = world.getBlockState(pos.up());
            if (above.isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                double x = pos.getX() + random.nextDouble();
                double z = pos.getZ() + random.nextDouble();
                double y = pos.getY() + 0.99;

                world.addParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, x, y, z, 0, 0, 0);
            }
        }
    }

    // Simplified portal creation - just fills a rectangular frame
    public static boolean tryCreatePortal(World world, BlockPos pos)
    {
        // Try both X and Z orientations
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z})
        {
            if (createPortalInOrientation(world, pos, axis))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean createPortalInOrientation(World world, BlockPos clickPos, Direction.Axis axis)
    {
        // Determine the directions for this axis
        Direction horizontalDir = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction horizontalDirNeg = horizontalDir.getOpposite();

        // Search for portal bounds
        BlockPos.Mutable pos = clickPos.mutableCopy();

        // Find bottom-left corner
        // First, go down to find bottom
        while (pos.getY() > world.getBottomY() && world.getBlockState(pos.down()).isOf(ModBlocks.VEIL_PORTAL_FRAME))
        {
            pos.move(Direction.DOWN);
        }

        // Then go to the negative horizontal direction to find edge
        while (world.getBlockState(pos.offset(horizontalDirNeg)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
        {
            pos.move(horizontalDirNeg);
        }

        BlockPos bottomLeft = pos.toImmutable();

        // Find dimensions
        int width = 0;
        BlockPos.Mutable scanner = bottomLeft.mutableCopy();
        while (world.getBlockState(scanner).isOf(ModBlocks.VEIL_PORTAL_FRAME) && width < 21)
        {
            scanner.move(horizontalDir);
            width++;
        }
        int height = 0;
        scanner.set(bottomLeft);
        while (world.getBlockState(scanner).isOf(ModBlocks.VEIL_PORTAL_FRAME) && height < 21)
        {
            scanner.move(Direction.UP);
            height++;
        }

        // Validate dimensions (need at least 4 wide, 5 tall for a 2x3 interior)
        if (width < 4 || height < 5)
        {
            return false;
        }

        // Verify it's a complete frame
        if (!isCompleteFrame(world, bottomLeft, width, height, horizontalDir))
        {
            return false;
        }

        // Fill with portal blocks
        BlockState portalState = ModBlocks.VEIL_PORTAL.getDefaultState().with(AXIS, axis);
        for (int x = 1; x < width - 1; x++)
        {
            for (int y = 1; y < height - 1; y++)
            {
                BlockPos fillPos = bottomLeft.offset(horizontalDir, x).up(y);
                if (world.getBlockState(fillPos).isAir())
                {
                    world.setBlockState(fillPos, portalState);
                }
            }
        }
        world.playSound(null, bottomLeft, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

        // Notify nearby players
        for (PlayerEntity nearbyPlayer : world.getPlayers())
        {
            if (nearbyPlayer.squaredDistanceTo(bottomLeft.getX(), bottomLeft.getY(), bottomLeft.getZ()) < 64.0)
            {
                nearbyPlayer.sendMessage(Text.translatable("chat.veildimension.portal_created"), true);
            }
        }
        return true;
    }

    private static boolean isCompleteFrame(World world, BlockPos bottomLeft, int width, int height, Direction horizontalDir)
    {
        // Check bottom row
        for (int x = 0; x < width; x++)
        {
            if (!world.getBlockState(bottomLeft.offset(horizontalDir, x)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                return false;
            }
        }

        // Check top row
        for (int x = 0; x < width; x++)
        {
            if (!world.getBlockState(bottomLeft.offset(horizontalDir, x).up(height - 1)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                return false;
            }
        }

        // Check left side
        for (int y = 0; y < height; y++)
        {
            if (!world.getBlockState(bottomLeft.up(y)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                return false;
            }
        }

        // Check right side
        for (int y = 0; y < height; y++)
        {
            if (!world.getBlockState(bottomLeft.offset(horizontalDir, width - 1).up(y)).isOf(ModBlocks.VEIL_PORTAL_FRAME))
            {
                return false;
            }
        }

        // Check interior is air
        for (int x = 1; x < width - 1; x++)
        {
            for (int y = 1; y < height - 1; y++)
            {
                if (!world.getBlockState(bottomLeft.offset(horizontalDir, x).up(y)).isAir())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static BlockPos findSafeSpawnPosition(ServerWorld world, BlockPos startPos)
    {
        BlockPos.Mutable pos = new BlockPos.Mutable(startPos.getX(), world.getTopY() - 1, startPos.getZ());

        // Start at build height and go down
        while (pos.getY() > world.getBottomY())
        {
            BlockState currentState = world.getBlockState(pos);
            BlockState aboveState = world.getBlockState(pos.up());
            BlockState twoAboveState = world.getBlockState(pos.up(2));

            // Found a solid block with 2 air blocks above
            if (currentState.isOpaque() && aboveState.isAir() && twoAboveState.isAir())
            {
                return pos.up().toImmutable();
            }

            pos.move(Direction.DOWN);
        }

        // Fallback to world spawn if no safe spot found
        return world.getSpawnPos();
    }
}