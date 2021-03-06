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


import code.elix_x.excomms.color.RGBA;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class IntegralIncrementerGuiElement<H extends IGuiElementsHandler<? extends IGuiElement<H>>> extends RectangularGuiElement<H> {

	protected int numberWidth;
	protected int buttonsWidth;

	protected int step;
	protected int min;
	protected int max;
	protected int value;

	public IntegralIncrementerGuiElement(String name, int xPos, int yPos, int numberWidth, int buttonsWidth, int height, int borderX, int borderY, int step, int min, int max, int defaultValue){
		super(name, xPos, yPos, numberWidth + buttonsWidth, height, borderX, borderY);
		this.numberWidth = numberWidth;
		this.buttonsWidth = buttonsWidth;

		this.step = step;
		this.min = min;
		this.max = max;
		this.value = defaultValue;
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		this.value = Math.max(Math.min(value, max), min);
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
		drawStringFull(gui.mc.fontRenderer, String.valueOf(getValue()), xPos + borderX, yPos + borderY + height - 8, new RGBA(1f, 1f, 1f, 1f));
		new GuiButtonExt(0, xPos + borderX + numberWidth, yPos + borderY, buttonsWidth, height / 2, "▲").drawButton(gui.mc, mouseX, mouseY, partialTicks);
		new GuiButtonExt(0, xPos + borderX + numberWidth, yPos + borderY + height / 2, buttonsWidth, height / 2, "▼").drawButton(gui.mc, mouseX, mouseY, partialTicks);
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
		if(down && key == 0 && inside(mouseX, mouseY)){
			if(mouseX >= xPos + borderX + numberWidth){
				if(mouseY < yPos + borderY + height / 2){
					setValue(getValue() + step);
				} else{
					setValue(getValue() - step);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleMouseEvent(H handler, GuiScreen gui, int mouseX, int mouseY, int dWheel){
		return false;
	}

}
