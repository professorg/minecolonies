package com.minecolonies.coremod.colony.management.requestsystem.token;

import com.minecolonies.coremod.colony.management.requestsystem.api.factory.IFactoryController;
import com.minecolonies.coremod.colony.management.requestsystem.api.token.ITokenFactory;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Factory for the standard request token.
 */
public class StandardTokenFactory implements ITokenFactory<UUID, StandardToken> {
    /**
     * Method to get the request type this factory can produce.
     *
     * @return The type of request this factory can produce.
     */
    @NotNull
    @Override
    public Class<? extends StandardToken> getFactoryOutputType() {
        return StandardToken.class;
    }

    /**
     * Used to determine which type of request this can produce.
     *
     * @return The class that represents the Type of Request this can produce.
     */
    @NotNull
    @Override
    public Class<? extends UUID> getFactoryInputType() {
        return UUID.class;
    }

    /**
     * Method to serialize a given constructable.
     *
     * @param controller The controller that can be used to serialize complicated types.
     * @param request    The request to serialize.
     * @return The serialized data of the given requets.
     */
    @NotNull
    @Override
    public NBTTagCompound serialize(@NotNull IFactoryController controller, @NotNull StandardToken request) {
        return request.serializeNBT();
    }

    /**
     * Method to deserialize a given constructable.
     *
     * @param controller The controller that can be used to deserialize complicated types.
     * @param nbt        The data of the request that should be deserialized.
     * @return The request that corresponds with the given data in the nbt
     */
    @NotNull
    @Override
    public StandardToken deserialize(@NotNull IFactoryController controller, @NotNull NBTTagCompound nbt) {
        StandardToken token = new StandardToken();
        token.deserializeNBT(nbt);
        return token;
    }

    /**
     * Method to get a new instance of a token given the input and token.
     *
     * @param input The input to build a new token for.
     * @return The new output instance for a given input.
     */
    @NotNull
    @Override
    public StandardToken getNewInstance(@NotNull UUID input) {
        return new StandardToken(input);
    }
}