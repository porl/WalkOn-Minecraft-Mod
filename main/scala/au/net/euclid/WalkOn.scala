package au.net.euclid

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLInitializationEvent


@Mod(modid = "walkOn", version = "0.0.0.0.0.0.1", name = "Walk On Speed Mod", modLanguage = "scala")
object WalkOn {

  @EventHandler
  def load(event: FMLInitializationEvent)
  {
    FMLCommonHandler.instance().bus().register(new PlayerTickHandler())
  }


}
