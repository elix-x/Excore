package code.elix_x.excore.client.thingy;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excore.client.debug.AdvancedDebugTools;
import code.elix_x.excore.client.thingy.ThingyData.Human;
import code.elix_x.excore.client.thingy.ThingyData.Human.Link;
import code.elix_x.excore.client.thingy.ThingyDisplay.MovingHuman;
import code.elix_x.excore.utils.client.gui.ElementalGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.*;
import code.elix_x.excore.utils.client.resource.PrecacheResourcePack;
import code.elix_x.excore.utils.client.resource.WebResourcePack;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class ThingyDisplay implements IGuiElementsHandler<MovingHuman> {

	private static final String DOMAIN = "excorethingy";

	private final String loc;
	private final ThingyData data;

	private final Random random;
	private final int chance;

	private Multimap<GuiScreen, MovingHuman> guiHumansMultimap = HashMultimap.create();

	private PrecacheResourcePack cacheRP;
	private boolean failed = false;

	private LoadingCache<String, ResourceLocation> cachedIcons = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<String, ResourceLocation>(){

		@Override
		public ResourceLocation load(String icon){
			icon = icon.replace(':', WebResourcePack.DEFCOLONCHAR);
			return new ResourceLocation(DOMAIN, icon.startsWith("http") ? icon : loc + icon);
		}

	});

	ThingyDisplay(String loc, ThingyData data, Random random, int chance){
		this.loc = loc;
		this.data = data;
		this.random = random;
		this.chance = chance;

		cacheRP = new PrecacheResourcePack(new WebResourcePack(DOMAIN));
		try{
			cacheRP.precache(getAllIcons());
		} catch(IOException e){
			failed = true;
		}

		if(!failed) AdvancedDebugTools.registerGUI(Keyboard.KEY_E, () -> { if(Minecraft.getMinecraft().currentScreen != null) regenHumans(Minecraft.getMinecraft().currentScreen); });
	}

	void cacheIcons(){
		if(failed) return;
		cacheRP.registerAsDefault();
		getAllIcons().forEach(icon -> Minecraft.getMinecraft().getTextureManager().loadTexture(icon, new SimpleTexture(icon)));
	}

	private ResourceLocation getCachedIcon(String icon){
		return cachedIcons.getUnchecked(icon);
	}

	private Stream<ResourceLocation> getAllIcons(){
		return Stream.concat(data.humans.stream().map(human -> human.icon), data.linkIcons.values().stream()).map(this::getCachedIcon);
	}

	private void regenHumans(GuiScreen gui){
		if(failed) return;
		guiHumansMultimap.removeAll(gui);
		List<Human> all = data.humans;
		List<Human> c = new ArrayList<>();
		int cc = 1 + random.nextInt(all.size());
		for(int i = 0; i < cc; i++){
			Human h = all.get(random.nextInt(all.size()));
			while(c.contains(h)) h = all.get(random.nextInt(all.size()));
			c.add(h);
		}
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		for(Human h : c){
			int size = 16 + random.nextInt(32);
			guiHumansMultimap.put(gui, new MovingHuman(h, size, size, new Vec3i(random.nextInt(res.getScaledWidth() - size), random.nextInt(res.getScaledHeight() - size), 0), new Vec3d(random.nextDouble(), random.nextDouble(), 0), 1 + random.nextInt(9), CollisionPhysics.values()[random.nextInt(CollisionPhysics.values().length)]));
		}
	}

	@SubscribeEvent
	public void open(GuiOpenEvent event){
		if(!(event.getGui() instanceof HumanGui) && !(Minecraft.getMinecraft().currentScreen instanceof HumanGui)){
			guiHumansMultimap.removeAll(Minecraft.getMinecraft().currentScreen);
			if(random.nextInt(chance) == 0){
				regenHumans(event.getGui());
			}
		}
	}

	@SubscribeEvent
	public void tick(GuiScreenEvent.DrawScreenEvent.Pre event){
		for(MovingHuman human : guiHumansMultimap.get(event.getGui())){
			human.update(new ScaledResolution(Minecraft.getMinecraft()));
		}
	}

	@SubscribeEvent
	public void draw(GuiScreenEvent.DrawScreenEvent.Post event){
		for(MovingHuman human : guiHumansMultimap.get(event.getGui())){
			human.drawGuiPost(this, event.getGui(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
		}
		for(MovingHuman human : guiHumansMultimap.get(event.getGui())){
			human.drawGuiPostPost(this, event.getGui(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
		}
	}

	@SubscribeEvent
	public void click(GuiScreenEvent.MouseInputEvent event){
		for(MovingHuman human : guiHumansMultimap.get(event.getGui())){
			if(human.handleMouseEvent(this, event.getGui(), GuiElement.mouseX(), GuiElement.mouseY(), Mouse.getEventButtonState(), Mouse.getEventButton()))
				event.setCanceled(true);
		}
	}

	@Override
	public void add(MovingHuman element){

	}

	@Override
	public MovingHuman getFocused(){
		return null;
	}

	@Override
	public void setFocused(MovingHuman element){

	}

	@Override
	public void looseFocus(){

	}

	public class MovingHuman extends ButtonGuiElement<ThingyDisplay> {

		private final Human human;

		private int sizeX = 32;
		private int sizeY = 32;

		private Vec3i pos;
		private Vec3d motion;
		private int speed;

		private CollisionPhysics physics;

		private ResourceLocation icon;

		public MovingHuman(Human human, int sizeX, int sizeY, Vec3i pos, Vec3d motion, int speed, CollisionPhysics physics){
			super(human.name, 0, 0, sizeX, sizeY, 0, 0, "");
			this.human = human;
			this.icon = getCachedIcon(human.icon);
			this.pos = pos;
			this.motion = motion;
			this.speed = speed;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.physics = physics;
		}

		public void update(ScaledResolution res){
			Vec3d pos = new Vec3d(this.pos);
			for(int i = 0; i < speed; i++){
				motion = physics.apply(random, res, pos, sizeX, sizeY, motion);
				pos = pos.add(motion);
			}

			if(pos.x + sizeX < 0 || res.getScaledWidth() < pos.x || pos.y + sizeY < 0 || res.getScaledHeight() < pos.y){
				pos = new Vec3d(random.nextInt(res.getScaledWidth() - sizeX), random.nextInt(res.getScaledHeight() - sizeY), 0);
			}

			this.pos = new Vec3i(pos.x, pos.y, pos.z);
		}

		@Override
		public void drawGuiPost(ThingyDisplay handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){
			xPos = this.pos.getX();
			yPos = this.pos.getY();
			resetButton();

			Minecraft.getMinecraft().renderEngine.bindTexture(icon);
			drawTexturedRect(toRectangle(), new Vec2f(0, 0), new Vec2f(1, 1));
		}

		@Override
		public void drawGuiPostPost(ThingyDisplay handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){
			if(inside(mouseX, mouseY))
				drawTooltipWithBackground(Minecraft.getMinecraft().fontRenderer, mouseX, mouseY, false, true, human.name);
		}

		@Override
		public void onButtonPressed(){
			super.onButtonPressed();
			Minecraft.getMinecraft().displayGuiScreen(new HumanGui(Minecraft.getMinecraft().currentScreen, human, icon));
		}

		@Override
		public String toString(){
			return "MovingHuman [human=" + human + ", sizeX=" + sizeX + ", sizeY=" + sizeY + ", pos=" + pos + ", motion=" + motion + ", speed=" + speed + ", physics=" + physics + ", icon=" + icon + "]";
		}

	}

	public class HumanGui extends ElementalGuiScreen {

		private final Human human;
		private final ResourceLocation icon;

		public HumanGui(GuiScreen parent, Human human, ResourceLocation icon){
			super(parent, 256, 256);
			this.human = human;
			this.icon = icon;
		}

		@Override
		protected void addElements(){
			add(new ColoredRectangleGuiElement<>("Shadow", 0, 0, width, height, 0, 0, new RGBA(0, 0, 0, 0.83f)));
			add(new TexturedRectangleGuiElement<>("Texture", xPos + 64, yPos, 128, 128, 0, 0, icon));
			if(human.getCategory(data).glint)
				add(new GlintRectangleGuiElement<>("Texture", xPos + 64, yPos, 128, 128, 0, 0, human.getCategory(data).color));
			nextY += 128 + 2;
			for(ITextComponent bio : human.bio){
				add(new CenteredStringGuiElement<>("Bio", xPos + 128, nextY, 2, 2, bio.getFormattedText(), fontRenderer, human.getCategory(data).color));
				nextY += 2 + 8 + 2;
			}
			List<Link> links = human.links;
			if(links != null && links.size() > 0){
				int nextX = xPos + 128 - (links.size() * 68) / 2;
				for(final Link link : links){
					add(new ButtonGuiElement("Link", nextX, nextY, 64, 64, 2, 2, link.url.toString()){

						@Override
						public void drawGuiPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){

						}

						@Override
						public void drawGuiPostPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY, float partialTicks){
							if(inside(mouseX, mouseY))
								drawTooltipWithBackground(fontRenderer, mouseX, mouseY, false, true, link.url.toString());
						}

						@Override
						public void onButtonPressed(){
							super.onButtonPressed();
							handleComponentClick(new TextComponentString("").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link.url.toString()))));
						}

					});
					add(new TexturedRectangleGuiElement<>("Link Texture", nextX, nextY, 64, 64, 2, 2, getCachedIcon(link.getIcon(data))));
					nextX += 68;
				}
			}

			parent.setGuiSize(width, height);
			parent.initGui();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks){
			parent.drawScreen(mouseX, mouseY, partialTicks);
			super.drawScreen(mouseX, mouseY, partialTicks);
		}

	}

	public enum CollisionPhysics {

		REFLECTION{

			@Override
			public Vec3d apply(Random random, ScaledResolution res, Vec3d pos, int sizeX, int sizeY, Vec3d motion){
				Vec3d norm = null;
				if(pos.x <= 0){
					norm = new Vec3d(1, 0, 0);
				} else if(pos.x >= res.getScaledWidth() - sizeX){
					norm = new Vec3d(-1, 0, 0);
				} else if(pos.y <= 0){
					norm = new Vec3d(0, 1, 0);
				} else if(pos.y >= res.getScaledHeight() - sizeY){
					norm = new Vec3d(0, -1, 0);
				}
				if(norm != null){
					motion = motion.subtract(mul(norm, motion.dotProduct(norm) * 2));
				}
				return motion;
			}

		},
		RANDOMONREFLECTION{

			@Override
			public Vec3d apply(Random random, ScaledResolution res, Vec3d pos, int sizeX, int sizeY, Vec3d motion){
				Vec3d norm = null;
				if(pos.x <= 0){
					norm = new Vec3d(1, 0, 0);
				} else if(pos.x >= res.getScaledWidth() - sizeX){
					norm = new Vec3d(-1, 0, 0);
				} else if(pos.y <= 0){
					norm = new Vec3d(0, 1, 0);
				} else if(pos.y >= res.getScaledHeight() - sizeY){
					norm = new Vec3d(0, -1, 0);
				}
				if(norm != null){
					motion = motion.subtract(mul(norm, motion.dotProduct(norm) * 2));
					float angle = -25 + random.nextInt(50);
					float cos = (float) Math.cos(Math.toRadians(angle));
					float sin = (float) Math.sin(Math.toRadians(angle));
					return new Vec3d(motion.x * cos - motion.y * sin, motion.x * sin + motion.y * cos, motion.z);
				}
				return motion;
			}

		},
		RANDOMREFLECTION{

			@Override
			public Vec3d apply(Random random, ScaledResolution res, Vec3d pos, int sizeX, int sizeY, Vec3d motion){
				float angle = -25 + random.nextInt(50);
				float cos = (float) Math.cos(Math.toRadians(angle));
				float sin = (float) Math.sin(Math.toRadians(angle));
				return REFLECTION.apply(random, res, pos, sizeX, sizeY, new Vec3d(motion.x * cos - motion.y * sin, motion.x * sin + motion.y * cos, motion.z));
			}

		};

		public abstract Vec3d apply(Random random, ScaledResolution res, Vec3d pos, int sizeX, int sizeY, Vec3d motion);

		private static Vec3d mul(Vec3d vec, double d){
			return new Vec3d(vec.x * d, vec.y * d, vec.z * d);
		}

	}

}
