package com.minecolonies.coremod.event;

import com.ldtteam.structures.blueprints.v1.Blueprint;
import com.ldtteam.structures.client.BlueprintRenderHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.configuration.Configurations;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.BlockUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.colony.CitizenDataView;
import com.minecolonies.coremod.colony.ColonyManager;
import com.minecolonies.coremod.colony.ColonyView;
import com.minecolonies.coremod.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.coremod.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.coremod.entity.EntityCitizen;
import com.minecolonies.coremod.entity.ai.basic.AbstractEntityAIStructure;
import com.minecolonies.coremod.entity.pathfinding.Pathfinding;
import com.minecolonies.coremod.items.ModItems;
import com.ldtteam.structures.helpers.Settings;
import com.ldtteam.structures.helpers.Structure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.Constants.BLOCKS_PER_CHUNK;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * Used to handle client events.
 */
public class ClientEventHandler
{
    /**
     * Seconds to show the citizen info sign.
     */
    private static final int SECONDS_TO_SHOW = 5;

    /**
     * The currently displayed citizen.
     */
    private CitizenDataView citizen = null;

    /**
     * The ticks passed since showing the sign.
     */
    private double ticksPassed = 0;

    /**
     * Cached wayPointBlueprint.
     */
    private Blueprint wayPointTemplate;

    /**
     * Cached wayPointBlueprint.
     */
    private Blueprint partolPointTemplate;

    /**
     * The colony view required here.
     */
    private ColonyView view = null;

    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     * @param event the catched event.
     */
    @SubscribeEvent
    public void renderWorldLastEvent(@NotNull final RenderWorldLastEvent event)
    {
        Pathfinding.debugDraw(event.getPartialTicks());

        final Structure structure = Settings.instance.getActiveStructure();
        final WorldClient world = Minecraft.getMinecraft().world;
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (structure != null)
        {
            final PlacementSettings settings = new PlacementSettings(Settings.instance.getMirror(), BlockPosUtil.getRotationFromRotations(Settings.instance.getRotation()));
            if (Settings.instance.getStructureName() != null && Settings.instance.getStructureName().contains(AbstractEntityAIStructure.WAYPOINT_STRING))
            {
                final ColonyView tempView = ColonyManager.getClosestColonyView(world, player.getPosition());
                if (tempView != null)
                {
                    if (wayPointTemplate == null)
                    {
                        wayPointTemplate = new Structure(world, "schematics/infrastructure/Waypoint", settings).getBluePrint();
                    }
                    BlueprintRenderHandler.getInstance().drawBlueprintAtListOfPositions(new ArrayList<>(tempView.getWayPoints()), event.getPartialTicks(), wayPointTemplate);
                }
            }
        }
        else if (player.getHeldItemMainhand().getItem() == ModItems.scepterGuard)
        {
            final PlacementSettings settings = new PlacementSettings(Settings.instance.getMirror(), BlockPosUtil.getRotationFromRotations(Settings.instance.getRotation()));
            final ItemStack stack = player.getHeldItemMainhand();
            if (!stack.hasTagCompound())
            {
                return;
            }
            final NBTTagCompound compound = stack.getTagCompound();

            final ColonyView colony = ColonyManager.getColonyView(compound.getInteger(TAG_ID), player.world.provider.getDimension());
            if (colony == null)
            {
                return;
            }

            final BlockPos guardTower = BlockPosUtil.readFromNBT(compound, TAG_POS);
            final AbstractBuildingView hut = colony.getBuilding(guardTower);

            if (partolPointTemplate == null)
            {
                partolPointTemplate = new Structure(world, "schematics/infrastructure/PatrolPoint", settings).getBluePrint();
            }

            if (hut instanceof AbstractBuildingGuards.View)
            {
                BlueprintRenderHandler.getInstance()
                  .drawBlueprintAtListOfPositions(((AbstractBuildingGuards.View) hut).getPatrolTargets().stream().map(BlockPos::up).collect(Collectors.toList()),
                    event.getPartialTicks(),
                    partolPointTemplate);
            }
        }
    }
}
