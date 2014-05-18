package au.net.euclid

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.init.Blocks

class PlayerTickHandler {

  //TODO: load from config file
  val sprintingQuickClimb = true // step up 1m when sprinting rather than 0.5m
  val walkOnMoveModifiers = Map("tile.stone" -> 0.4, "tile.stonebrick" -> 0.2, "tile.grass" -> -0.2, "tile.sand" -> -0.4) //current values exaggerated for ease of testing

  val walkOnMoveModMap = walkOnMoveModifiers.map {
    case (name, value) => (name, new AttributeModifier("WalkOn " + name + " Movement Modifier", value, 1).setSaved(false))
  }

  var currentBlockUnderFeet = Blocks.air

  val delayTick = 5 //ticks to wait after value change to avoid modifier springing
  var ticksToWait = 0

  @SubscribeEvent
  def onPlayerTick(event: PlayerTickEvent) {
    val player: EntityPlayer = event.player

    val movement = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed)

    val x = Math.floor(player.posX).toInt
    val y = (player.posY - player.getYOffset).toInt
    val z = Math.floor(player.posZ).toInt

    val lastBlockUnderFeet = currentBlockUnderFeet
    currentBlockUnderFeet = player.worldObj.getBlock(x, y - 1, z)
    if (currentBlockUnderFeet == Blocks.air) currentBlockUnderFeet = lastBlockUnderFeet // ignore jumps

    if (ticksToWait <= 0) {
      if (lastBlockUnderFeet != currentBlockUnderFeet) {
        println("changed from " + lastBlockUnderFeet.getUnlocalizedName + " to " + currentBlockUnderFeet.getUnlocalizedName)

        val lastBlockName = lastBlockUnderFeet.getUnlocalizedName
        val currentBlockName = currentBlockUnderFeet.getUnlocalizedName

        //remove previous walkon mod
        if (walkOnMoveModMap.contains(lastBlockName)) {
          if (movement.getModifier(walkOnMoveModMap(lastBlockName).getID) != null) {
            movement.removeModifier(walkOnMoveModMap(lastBlockName))
            println("Removed " + lastBlockName + " Speed Modifier")
          }
        }

        // add new walkon mod
        if (walkOnMoveModMap.contains(currentBlockName)) {
          if (movement.getModifier(walkOnMoveModMap(currentBlockName).getID) == null) {
            movement.applyModifier(walkOnMoveModMap(currentBlockName))
            println("Applied " + currentBlockName + " Speed Modifier")
          }
        }

        ticksToWait = 1
      }
    } else {
      ticksToWait -= 1
    }

    if (sprintingQuickClimb) {
      if (player.isSprinting) {
        player.stepHeight = 1.0F
      } else {
        player.stepHeight = 0.5F
      }
    }


  }
}
