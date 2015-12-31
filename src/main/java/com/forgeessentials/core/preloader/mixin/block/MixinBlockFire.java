package com.forgeessentials.core.preloader.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fe.event.world.FireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire extends Block
{

    public MixinBlockFire(Material material)
    {
        super(material);
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z") , cancellable = true, remap = false)
    public void handleTryCatchFireA(World world, int x, int y, int z, int chance, Random p_149841_6_, int argValue1, ForgeDirection face, CallbackInfo ci)
    {
        System.out.println("Mixin : Fire destroyed block and spread to below block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(x, y, z, world, this, argValue1)))
        {
            ci.cancel();
        }
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z") , cancellable = true, remap = false)
    public void handleTryCatchFireB(World world, int x, int y, int z, int chance, Random rnd, int argValue1, ForgeDirection face, CallbackInfo ci)
    {
        System.out.println("Mixin : Fire destroyed block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(x, y, z, world, this, argValue1)))
        {
            ci.cancel();
        }
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z") , cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void updateTick(World world, int sourceX, int sourceY, int sourceZ, Random rnd, CallbackInfo ci, boolean isFireSource, int blockMeta,
            boolean isHighHumidity, byte b0, int x, int y, int z)
    {
        System.out.println(String.format("Mixin : Fire spreading to other block from [%d,%d,%d] to [%d,%d,%d]", sourceX, sourceY, sourceZ, x, y, z));
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(x, y, z, world, this, 3, sourceX, sourceY, sourceZ)))
        {
            ci.cancel();
        }
    }

}
