/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.runecraft;

import com.google.common.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;
import net.runelite.client.RuneLite;
import net.runelite.client.events.ChatMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;

@PluginDescriptor(
	name = "Runecraft plugin"
)
public class Runecraft extends Plugin
{
	private static Pattern bindNeckString = Pattern.compile("You have ([0-9]+) charges left before your Binding necklace disintegrates.");

	private final RunecraftConfig config = RuneLite.getRunelite().getConfigManager().getConfig(RunecraftConfig.class);
	private final RunecraftOverlay overlay = new RunecraftOverlay(this);
	private final BindNeckOverlay bindNeckOverlay = new BindNeckOverlay(this);

	@Override
	public Collection<Overlay> getOverlays()
	{
		return Arrays.asList(overlay, bindNeckOverlay);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	public RunecraftConfig getConfig()
	{
		return config;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SERVER)
		{
			return;
		}

		Matcher match = bindNeckString.matcher(event.getMessage());
		if (match.find())
		{
			bindNeckOverlay.bindingCharges = Integer.parseInt(match.group(1));
			return;
		}

		if (event.getMessage().contains("You bind the temple's power"))
		{
			if (event.getMessage().contains("mud")
					|| event.getMessage().contains("lava")
					|| event.getMessage().contains("steam")
					|| event.getMessage().contains("dust")
					|| event.getMessage().contains("smoke")
					|| event.getMessage().contains("mist"))
			{
				bindNeckOverlay.bindingCharges -= 1;
				return;
			}
		}

		if (event.getMessage().contains("Your Binding necklace has disintegrated."))
		{
			//set it to 17 because this message is triggered first before the above chat event
			bindNeckOverlay.bindingCharges = 17;
		}
	}
}