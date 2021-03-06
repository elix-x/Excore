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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

public class GlintRectangleGuiElement<H extends IGuiElementsHandler<? extends IGuiElement<H>>> extends RectangularGuiElement<H> {

	public static final ResourceLocation DEFAULTTEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	public static final RGBA DEFAULTCOLOR = RGBA.fromARGB(-8372020);

	protected ResourceLocation glint;

	protected RGBA color;

	public GlintRectangleGuiElement(String name, int xPos, int yPos, int width, int height, int borderX, int borderY, ResourceLocation glint, RGBA color){
		super(name, xPos, yPos, width, height, borderX, borderY);
		this.glint = glint;
		this.color = color;
	}

	public GlintRectangleGuiElement(String name, int xPos, int yPos, int width, int height, int borderX, int borderY, RGBA color){
		this(name, xPos, yPos, width, height, borderX, borderY, DEFAULTTEXTURE, color);
	}

	public GlintRectangleGuiElement(String name, int xPos, int yPos, int width, int height, int borderX, int borderY, ResourceLocation glint){
		this(name, xPos, yPos, width, height, borderX, borderY, glint, DEFAULTCOLOR);
	}

	public GlintRectangleGuiElement(String name, int xPos, int yPos, int width, int height, int borderX, int borderY){
		this(name, xPos, yPos, width, height, borderX, borderY, DEFAULTTEXTURE, DEFAULTCOLOR);
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
		Rectangle element = toInnerRectangle();
		float factor = 1 / (float) (Math.sqrt(element.getWidth() * element.getHeight()));

		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		gui.mc.getTextureManager().bindTexture(glint);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.scale(factor, factor, factor);
		float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / factor;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexbuffer.pos(element.getX(), bottom(element), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(right(element), bottom(element), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(right(element), element.getY(), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(element.getX(), element.getY(), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		tessellator.draw();

		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(factor, factor, factor);
		float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / factor;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexbuffer.pos(element.getX(), bottom(element), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(right(element), bottom(element), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(right(element), element.getY(), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		vertexbuffer.pos(element.getX(), element.getY(), 0).tex(0, 1).color(color.getRF(), color.getGF(), color.getBF(), color.getAF()).endVertex();
		tessellator.draw();

		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
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
		return false;
	}

	@Override
	public boolean handleMouseEvent(H handler, GuiScreen gui, int mouseX, int mouseY, int dWheel){
		return false;
	}

}
