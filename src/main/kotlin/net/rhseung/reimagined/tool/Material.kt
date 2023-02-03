package net.rhseung.reimagined.tool

import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.rhseung.reimagined.utils.Color
import java.lang.Integer.min
import kotlin.math.pow

enum class Material(
	val color: Color,
	
	val stat: Int,
	val weight: Int,
	val trait: Trait? = null,
	
	val canHead: Boolean = false,
	val canBinding: Boolean = false,
	val canHandle: Boolean = false,
	
	val isMetal: Boolean = true,
	
	val repairIngredient: Ingredient,
	
	/** for not vanilla materials */
	val worldSpawn: Boolean = false
) {
	Wood( /** durability: 32 */
		color = Color(150, 116, 65), stat = 0, weight = 1,
		isMetal = false,
		canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.OAK_PLANKS)
	),
	Stone( /** durability: 88 */
		color = Color(149, 145, 141), stat = 1, weight = 2,
		isMetal = false,
		canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.COBBLESTONE)
	),
	Copper( /** durability: 263 */
		color = Color(202, 118, 91), stat = 2, weight = 4,
		canHead = true, canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.COPPER_INGOT)
	),
	Iron( /** durability: 534 */
		color = Color(215, 215, 215), stat = 3, weight = 5,
		canHead = true, canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.IRON_INGOT)
	),
	Diamond( /** durability: 864 */
		color = Color(43, 199, 172), stat = 4, weight = 3,
		canHead = true, canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.DIAMOND)
	),  // todo: 다이아몬드는 보석류이므로 나중에 없앨거임
	//      - 강철로 할 것 같음
	//      - 용광로(->합금제조기)랑 훈연기(->코크오븐) 오버라이딩해서 만들 예정
	Netherite( /** durability: 1501 */
		color = Color(134, 123, 134), stat = 5, weight = 5,
		trait = Trait.Fireproof,
		canHead = true, canHandle = true,
		repairIngredient = Ingredient.ofItems(Items.NETHERITE_INGOT)
	);
	
	fun getStat(stat: Stat): Number {
		return when (stat) {
			Stat.DURABILITY -> getDurability()
			Stat.ATTACK_DAMAGE -> getAttackDamage()
			Stat.ATTACK_SPEED -> getAttackSpeed()
			Stat.MINING_TIER -> getMiningTier()
			Stat.MINING_SPEED -> getMiningSpeed()
			Stat.ENCHANTABILITY -> getEnchantability()
			// todo: 다른 스탯 cast도 지원하기
		}
	}
	
	fun getDurability() = (24 * stat.toFloat().pow(2.5F) + 32 * weight).toInt()
	fun getAttackDamage() = 0.6F * stat + 0.1F * weight
	fun getAttackSpeed() = 1.6F - 0.6F * weight
	fun getMiningTier() = stat
	fun getMiningSpeed() = 1 + 2 * stat - 0.4F * weight
	fun getEnchantability() = min(7 + 3*((stat + weight) % 5) - (0.2F * weight).toInt(), 25)
	// todo: 다른 스탯 공식도 만들기
	
	/** note:
	 *  - stats
	 *   durability(s, w) = 24s^2.5 + 32w
	 *   attack_damage(s, w) = 0.6s + 0.1w
	 *   attack_speed(s, w) = 1.6 - 0.6w
	 *   mining_tier(s, w) = s
	 *   mining_speed(s, w) = 1 + 2s - 0.4w
	 *   enchantability(s, w) = min(7 + 3((s + w) % 5) - int(0.2w), 25)
	 */
	
	companion object {
		fun getColor(stat: Int) = when (stat) {
			0 -> Wood.color
			1 -> Stone.color
            2 -> Copper.color
			3 -> Iron.color
            4 -> Diamond.color
			5 -> Netherite.color
			else -> Color.WHITE
		}
	}
}