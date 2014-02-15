package org.mcsg.survivalgames.util;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mcsg.survivalgames.SettingsManager;
import org.mcsg.survivalgames.SurvivalGames;

public class ChestRatioStorage {

	public static ChestRatioStorage instance = new ChestRatioStorage();
	private ArrayList<Chest> chests = new ArrayList<Chest>();

	private ChestRatioStorage() { 
		
	}

	public static ChestRatioStorage getInstance(){
		return instance;
	}
	
	public void setup(){

                chests = new ArrayList<Chest>(); // Make new chests
                
		File chestFile = SettingsManager.getInstance().getChestFile();

		try {
			
			JSONParser parser = new JSONParser();			
			JSONObject root = (JSONObject)parser.parse(new FileReader(chestFile));
			
			JSONArray jsonChests = (JSONArray)root.get("chests");
			for (Object chestObject : jsonChests) {
				
				JSONObject chest = (JSONObject)chestObject;
				double chance = (Double) chest.get("chance");
				
				ArrayList<ItemStack> chestContents = new ArrayList<ItemStack>();
				JSONArray contents = (JSONArray) chest.get("items");
				for (Object itemObject : contents) {
                                        try {
                                            ItemStack item = parseChestItem((JSONObject)itemObject);	
                                            if (item != null) {
                                            	chestContents.add(item);
                                            }
                                        }
                                        catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
				}				
				
				Chest newChest = new Chest();
				newChest.setChance(chance);
				newChest.setContents(chestContents);			
				this.chests.add(newChest);
			}
			
		} catch (Exception ex) {
                    SurvivalGames.$(Level.WARNING, "Failed To Load Item Config!", ex);
		}
		
	}
	
	private ItemStack parseChestItem(JSONObject itemObject) {
		
		// We have to get a valid material. If we don't output an error and return null
		Material itemMaterial = Material.AIR;
		try {
			if (!itemObject.containsKey("Material")) {
				SurvivalGames.$(Level.SEVERE, "Item in chest does not have required material parameter!");
				return null;
			}
			
			itemMaterial = Material.valueOf((String)itemObject.get("Material"));
			
		} catch(Exception ex) {
			SurvivalGames.$(Level.WARNING, "Failed to get material for '" + (String)itemObject.get("Material") + "'.");
			return null;
		}
		
		// amount if specified
		Long stackSize = 1L;
				
		if (itemObject.containsKey("Amount")) {
			stackSize = (Long)itemObject.get("Amount");
		}
                
                Long dataValue = null;
                if (itemObject.containsKey("Data")) {
			dataValue = (Long)itemObject.get("Data");
		}
		
		// Create the item stack.
		ItemStack item = null;
                if (dataValue == null) {
                    item = new ItemStack(itemMaterial, stackSize.intValue());
                } else {
                    item = new ItemStack(itemMaterial, stackSize.intValue(), dataValue.shortValue());
                }

		if (itemObject.containsKey("Damage")) {
			Long damageValue = (Long)itemObject.get("Damage");
			short maxDamage = itemMaterial.getMaxDurability();
			
			short actualDurability = (short) (((float)maxDamage) * (damageValue.floatValue() / 100.0f));
			item.setDurability(actualDurability);
		}
		
		// Get the meta data so we can update it
		ItemMeta meta = item.getItemMeta();
		
		// Try and set the items name
		if (itemObject.containsKey("Name")) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String)itemObject.get("Name")));
		}
		/////////////////////////////////////////
		
		// Set the item lore
		if (itemObject.containsKey("Lore")) {
			ArrayList<String> lore = new ArrayList<String>();
			
			JSONArray loreArray = (JSONArray)itemObject.get("Lore");
			for (Object loreObject : loreArray) {
				String loreLine = ChatColor.translateAlternateColorCodes('&', (String)loreObject);
				lore.add(loreLine);
			}
			
			if (!lore.isEmpty()) {
				meta.setLore(lore);
			}
		}
		/////////////////////////////////////////
		
		// Enchantment's
		if (itemObject.containsKey("Enchantments")) {
			JSONArray enchantmentArray = (JSONArray)itemObject.get("Enchantments");
			for (Object enchantmentObject : enchantmentArray) {
				JSONObject jsonEnchantment = (JSONObject)enchantmentObject;
				
				String enchantmentName = (String)jsonEnchantment.get("Name");
				Long enchantmentLevel = (Long)jsonEnchantment.get("Level");
				
				Enchantment enchantment = Enchantment.getByName(enchantmentName);
				if (enchantment != null && enchantmentLevel != null) {
					meta.addEnchant(enchantment, enchantmentLevel.intValue(), true);
				}
			}
		}
		/////////////////////////////////////////

		item.setItemMeta(meta);
                
                // if the item is dyable, get the color
                if (isDyableArmour(itemMaterial)) {
                    if (itemObject.containsKey("Color")) {
                        try {
                            Color dyeColor = Color.fromRGB(((Long)itemObject.get("Color")).intValue());
                            LeatherArmorMeta armourMeta = (LeatherArmorMeta)item.getItemMeta();
                            armourMeta.setColor(dyeColor);
                            item.setItemMeta(armourMeta);
                            SurvivalGames.$("Colored Item: " + item.getType().name() + " - " + dyeColor.asRGB());
                        } catch (Exception ex) {
                            SurvivalGames.$(Level.WARNING, "Item '" + itemMaterial.name() + "' has color option, but invalid color was specified.", ex);
                        }
                    }
                }
                ////////////////////////////////////////
                
		return item;
	}

        private boolean isDyableArmour(Material material) {
            if (material == Material.LEATHER_BOOTS)
                return true;
            if (material == Material.LEATHER_CHESTPLATE)
                return true;
            if (material == Material.LEATHER_HELMET)
                return true;
            if (material == Material.LEATHER_LEGGINGS)
                return true;
            
            return false;
        }
        
	public ArrayList<ItemStack> getItems() {
		
		Random random = new Random();
		int noofItems = random.nextInt(5) + 1;
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		Chest commonChest = null;
		double chance = 0.0;
		for (Chest chest : chests) {
			if (chest.getChance() > chance) {
				chance = chest.getChance();
				commonChest = chest;
			}
		}
		
		int loopSafty = 500 / chests.size();
		while (noofItems != 0) {
			
			Chest chestToUse = null;
			while (chestToUse == null) {
				
				if (loopSafty <= 0) {
					chestToUse = commonChest;
					break;
				}
				
				for (Chest chest : chests) {
					if (chest.useThisChest(random)) {
						chestToUse = chest;
						break;
					}
				}
				loopSafty--;
			}
			
			items.add(chestToUse.getRandomItem(random));
			
			noofItems--;
		}
		
		return items;
	}

}