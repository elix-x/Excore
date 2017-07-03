package code.elix_x.excore.utils.client.render.world;

import code.elix_x.excore.utils.client.render.OpenGLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class LargeBlockAccessRenderer {

	public static int findOptimalBlockDimension(int size, int maxBlock, int minBlock){
		for(int next = maxBlock; next >= minBlock; next--) if(size % next == 0) return next;
		return maxBlock;
	}

	private final IBlockAccess world;
	// Shapes API has to be rewritten to support IBlockAccesses
	// private final Shape3D shape;
	private final AxisAlignedBB shape;
	private final AxisAlignedBB shapeResult;
	private final Vec3i renderBlock;

	private final List<BlockAccessRenderer> renderers = new ArrayList<>();

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, AxisAlignedBB shapeResult, Vec3i renderBlock){
		this.world = world;
		this.shape = shape.expand(0.5, 0.5, 0.5).offset(0.5, 0.5, 0.5);
		this.shapeResult = shapeResult;
		this.renderBlock = renderBlock;

		initRenderers();
	}

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3d posResult, Vec3i renderBlock){
		this(world, shape, shape.offset(posResult.subtract(shape.getCenter())), renderBlock);
	}

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3i renderBlock){
		this(world, shape, shape, renderBlock);
	}

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, AxisAlignedBB shapeResult){
		this.world = world;
		this.shape = shape.expand(0.5, 0.5, 0.5).offset(0.5, 0.5, 0.5);
		this.shapeResult = shapeResult;
		this.renderBlock = new Vec3i(findOptimalBlockDimension((int) (this.shape.maxX - this.shape.minX), 16, 4), findOptimalBlockDimension((int) (this.shape.maxY - this.shape.minY), 16, 4), findOptimalBlockDimension((int) (this.shape.maxZ - this.shape.minZ), 16, 4));

		initRenderers();
	}

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3d posResult){
		this(world, shape, shape.offset(posResult.subtract(shape.getCenter())));
	}

	public LargeBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape){
		this(world, shape, shape);
	}

	void initRenderers(){
		renderers.clear();
		for(int x = 0; x < Math.ceil((shape.maxX - shape.minX) / renderBlock.getX()); x += renderBlock.getX()){
			for(int y = 0; y < Math.ceil((shape.maxY - shape.minY) / renderBlock.getY()); y += renderBlock.getY()){
				for(int z = 0; z < Math.ceil((shape.maxZ - shape.minZ) / renderBlock.getZ()); z += renderBlock.getZ()){
					renderers.add(new BlockAccessRenderer(world, new AxisAlignedBB(shape.minX + x * renderBlock.getX(), shape.minY + y * renderBlock.getY(), shape.minZ + z * renderBlock.getZ(), Math.max(shape.minX + (x + 1) * renderBlock.getX(), shape.maxX), Math.max(shape.minY + (y + 1) * renderBlock.getY(), shape.maxY), Math.max(shape.minZ + (z + 1) * renderBlock.getZ(), shape.maxZ))));
				}
			}
		}
	}

	public void markDirty(AxisAlignedBB region){
		for(BlockAccessRenderer renderer : renderers) if(renderer.shape.intersects(region)) renderer.markDirty();
	}

	public void markDirty(BlockPos region){
		markDirty(new AxisAlignedBB(region.add(-1, -1, -1), region.add(1, 1, 1)));
	}

	public void render(){
		updateCheck();
		renderSetup();
		renderPre();
		for(BlockRenderLayer layer : BlockRenderLayer.values()){
			renderLayerSetup(layer);
			renderLayer(layer);
			renderLayerCleanup(layer);
		}
		renderPost();
		renderCleanup();
	}

	void updateCheck(){

	}

	void renderSetup(){
		OpenGLHelper.enableClientState(DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}

	void renderPre(){
		GlStateManager.pushMatrix();
		double scaleX = (shapeResult.maxX - shapeResult.minX) / (shape.maxX - shape.minX);
		double scaleY = (shapeResult.maxY - shapeResult.minY) / (shape.maxY - shape.minY);
		double scaleZ = (shapeResult.maxZ - shapeResult.minZ) / (shape.maxZ - shape.minZ);
		GlStateManager.scale(scaleX, scaleY, scaleZ);
		GlStateManager.translate(shapeResult.getCenter().x / scaleX, shapeResult.getCenter().y / scaleY, shapeResult.getCenter().z / scaleZ);
	}

	void renderLayerSetup(BlockRenderLayer layer){
		//TODO Implement
	}

	void renderLayer(BlockRenderLayer layer){
		renderers.forEach(renderer -> renderer.renderLayer(layer));
	}

	void renderLayerCleanup(BlockRenderLayer layer){
		//TODO Implement
	}

	void renderPost(){
		GlStateManager.popMatrix();
	}

	void renderCleanup(){
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		RenderHelper.enableStandardItemLighting();
		OpenGLHelper.disableClientState(DefaultVertexFormats.BLOCK);
	}

	protected void cleanUp(){
		renderers.forEach(BlockAccessRenderer::cleanUp);
	}

}