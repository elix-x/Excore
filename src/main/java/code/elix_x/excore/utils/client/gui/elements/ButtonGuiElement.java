/*******************************************************************************
 * Copyright 2016 Elix_x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package code.elix_x.excore.utils.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ButtonGuiElement<H extends IGuiElementsHandler<? extends IGuiElement<H>>> extends RectangularGuiElement<H> {

	protected GuiButtonExt button;

	public ButtonGuiElement(String name, int xPos, int yPos, int width, int height, int borderX, int borderY, String buttonText){
		super(name, xPos, yPos, width, height, borderX, borderY);
		this.button = new GuiButtonExt(0, xPos + borderX, yPos + borderY, width, height, buttonText);
	}

	public void resetButton(){
		this.button = new GuiButtonExt(0, xPos + borderX, yPos + borderY, width, height, button.displayString);
	}

	@Override
	public void openGui(H handler, GuiScreen gui){

	}

	@Override
	public void initGui(H handler, GuiScreen gui){

	}

	@Override
	public void drawGuiPre(H handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){

	}

	@Override
	public void drawBackground(H handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){

	}

	@Override
	public void drawGuiPost(H handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){
		button.drawButton(gui.mc, mouseX, mouseY, partialTicks);
	}

	@Override
	public void drawGuiPostPost(H handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){

	}

	@Override
	public boolean handleKeyboardEvent(H handler, GuiScreen gui, boolean down, int key, char c){
		return false;
	}

	@Override
	public boolean handleMouseEvent(H handler, GuiScreen gui, int mouseX, int mouseY, boolean down, int key){
		if(down && key == 0 && button.mousePressed(gui.mc, mouseX, mouseY)){
			onButtonPressed();
			return true;
		}
		return false;
	}

	public void onButtonPressed(){
		button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
	}

	@Override
	public boolean handleMouseEvent(H handler, GuiScreen gui, int mouseX, int mouseY, int dWheel){
		return false;
	}

}
