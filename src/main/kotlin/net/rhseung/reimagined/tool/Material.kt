package net.rhseung.reimagined.tool

import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Color
import net.rhseung.reimagined.utils.Math.pow
import java.lang.Integer.min

enum class Material(
	val color: Color,
	
	val stat: Int,
	val weight: Int,
	val trait: Trait? = null,
	val canParts: List<PartType>,
	
	val isMetal: Boolean = true,
	
	val repairIngredient: Ingredient,
	
	/** for not vanilla materials */
	val worldSpawn: Boolean = false
) {
	DUMMY(
		color = Color.WHITE, stat = -1, weight = -1, isMetal = false,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.BINDING, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.CHAIN)
	),
	WOOD( /** durability: 32 */
		color = Color(150, 116, 65), stat = 0, weight = 1, isMetal = false,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.OAK_PLANKS)
	),
	STONE( /** durability: 88 */
		color = Color(149, 145, 141), stat = 1, weight = 2, isMetal = false,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.COBBLESTONE)
	),
	COPPER( /** durability: 320 */
		color = Color(202, 118, 91), stat = 2, weight = 4,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.BINDING, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.COPPER_INGOT)
	),
	IRON( /** durability: 808 */
		color = Color(215, 215, 215), stat = 3, weight = 5,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.BINDING, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.IRON_INGOT)
	),
	DIAMOND( /** durability: 1632 */
		color = Color(43, 199, 172), stat = 4, weight = 3,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.BINDING, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.DIAMOND)
	),  // todofar: 다이아몬드는 보석류이므로 나중에 없앨거임
	//      - 강철로 할 것 같음
	//      - 용광로(->합금제조기)랑 훈연기(->코크오븐) 오버라이딩해서 만들 예정
	NETHERITE( /** durability: 3160 */
		color = Color(134, 123, 134), stat = 5, weight = 5, trait = Trait.Fireproof,
		canParts = listOf(PartType.PICKAXE_HEAD, PartType.BINDING, PartType.HANDLE),
		repairIngredient = Ingredient.ofItems(Items.NETHERITE_INGOT)
	);
	
	fun getStat(s: Stat): Float {
		return when (s) {
			Stat.DURABILITY -> if (stat == -1) s.defaultValue else getDurability()
			Stat.ATTACK_DAMAGE -> if (stat == -1) s.defaultValue else getAttackDamage()
			Stat.ATTACK_SPEED -> if (stat == -1) s.defaultValue else getAttackSpeed()
			Stat.MINING_TIER -> if (stat == -1) s.defaultValue else getMiningTier()
			Stat.MINING_SPEED -> if (stat == -1) s.defaultValue else getMiningSpeed()
			Stat.ENCHANTABILITY -> if (stat == -1) s.defaultValue else getEnchantability()
			// todofar: 다른 스탯 cast도 지원하기
		}
	}
	
	fun getDurability() = (24 * stat.pow(3) + 32 * weight).toFloat()
	fun getAttackDamage() = 1.0F * stat + 0.2F * weight
	fun getAttackSpeed() = -0.1F * (weight - 3)
	fun getMiningTier() = stat.toFloat()
	fun getMiningSpeed() = 2 + 2.5F * stat - 0.3F * weight
	fun getEnchantability() = min(7 + 3*((stat + weight) % 5) - (0.2F * weight).toInt(), 25).toFloat()
	// todofar: 다른 스탯 공식도 만들기
	
	/** note:
	 *   - stats
	 *   durability(s, w) = 24s^2.5 + 32w
	 *   attack_damage(s, w) = 0.6s + 0.1w
	 *   attack_speed(s, w) = 1.6 - 0.6w
	 *   mining_tier(s, w) = s
	 *   mining_speed(s, w) = 1 + 2s - 0.4w
	 *   enchantability(s, w) = min(7 + 3((s + w) % 5) - int(0.2w), 25)
	 */
	
	companion object {
		val MAX_TIER = 5
		
		fun get(miningLevel: Int) = when (miningLevel) {
			0 -> WOOD
			1 -> STONE
			2 -> COPPER
			3 -> IRON
			4 -> DIAMOND
			5 -> NETHERITE
			else -> DUMMY
			// todofar: 다른 material도 지원하기
		}
		
		fun getColor(miningLevel: Int): Color {
			return get(miningLevel).color
		}
	}
}