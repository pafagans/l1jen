/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SkillTable;
import l1j.server.server.encryptions.IdFactory;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1MobSkill;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skill;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;

public class L1MobSkillUse {
	private static Logger _log = Logger
			.getLogger(L1MobSkillUse.class.getName());

	private L1MobSkill _mobSkillTemplate = null;
	private L1NpcInstance _attacker = null;
	private WeakReference<L1Character> _target = null;
	private Random _rnd = new Random();
	private int _sleepTime = 0;
	private int _skillUseCount[];

	public L1MobSkillUse(L1NpcInstance npc) {
		_sleepTime = 0;

		_mobSkillTemplate = MobSkillTable.getInstance().getTemplate(
				npc.getNpcTemplate().get_npcId());
		if (_mobSkillTemplate == null) {
			return;
		}
		_attacker = npc;
		_skillUseCount = new int[_mobSkillTemplate.getSkillSize()];
	}

	private int getSkillUseCount(int idx) {
		return _skillUseCount[idx];
	}

	private void skillUseCountUp(int idx) {
		_skillUseCount[idx]++;
	}

	public void resetAllSkillUseCount() {
		if (_mobSkillTemplate == null) {
			return;
		}

		for (int i = 0; i < _mobSkillTemplate.getSkillSize(); i++) {
			_skillUseCount[i] = 0;
		}
	}

	public int getSleepTime() {
		return _sleepTime;
	}

	public void setSleepTime(int i) {
		_sleepTime = i;
	}

	public boolean skillUse(L1Character tg) {
		if (_mobSkillTemplate == null) {
			return false;
		}
		_target = new WeakReference<L1Character>(tg);

		int type = _mobSkillTemplate.getType(0);

		if (type == L1MobSkill.TYPE_NONE) {
			return false;
		}

		for (int i = 0; i < _mobSkillTemplate.getSkillSize()
				&& _mobSkillTemplate.getType(i) != L1MobSkill.TYPE_NONE; i++) {

			int changeType = _mobSkillTemplate.getChangeTarget(i);
			if (changeType > 0) {
				_target = new WeakReference<L1Character>(
						changeTarget(changeType, i));
			}

			if (isSkillUseble(i) == false) {
				continue;
			}

			type = _mobSkillTemplate.getType(i);
			if (type == L1MobSkill.TYPE_PHYSICAL_ATTACK) {
				if (physicalAttack(i) == true) {
					skillUseCountUp(i);
					return true;
				}
			} else if (type == L1MobSkill.TYPE_MAGIC_ATTACK) {
				if (magicAttack(i) == true) {
					skillUseCountUp(i);
					return true;
				}
			} else if (type == L1MobSkill.TYPE_SUMMON) {
				if (summon(i) == true) {
					skillUseCountUp(i);
					return true;
				}
			} else if (type == L1MobSkill.TYPE_POLY) {
				if (poly(i) == true) {
					skillUseCountUp(i);
					return true;
				}
			}
		}

		return false;
	}

	private boolean summon(int idx) {
		int summonId = _mobSkillTemplate.getSummon(idx);
		int min = _mobSkillTemplate.getSummonMin(idx);
		int max = _mobSkillTemplate.getSummonMax(idx);

		if (summonId == 0) {
			return false;
		}

		int count = _rnd.nextInt(max) + min;
		mobspawn(summonId, count);

		_attacker.broadcastPacket(new S_SkillSound(_attacker.getId(), 761));
		_attacker.broadcastPacket(new S_DoActionGFX(_attacker.getId(),
					ActionCodes.ACTION_SkillBuff));

		_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		return true;
	}

	private boolean poly(int idx) {
		int polyId = _mobSkillTemplate.getPolyId(idx);
		boolean usePoly = false;

		if (polyId == 0) {
			return false;
		}

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(_attacker)) {
			if (pc.isDead() || pc.isGhost() || pc.isGmInvis()) { 
				continue;
			}
			if (_attacker.glanceCheck(pc.getX(), pc.getY()) == false) {
				continue; 
			}

			int npcId = _attacker.getNpcTemplate().get_npcId();
			switch (npcId) {
				case 81082: 
					pc.getInventory().takeoffEquip(945);
					break;
				default:
					break;
			}
			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
			usePoly = true;
		}
		if (usePoly) {
			for (L1PcInstance pc :
					L1World.getInstance().getVisiblePlayer(_attacker)) {
				pc.sendAndBroadcast(new S_SkillSound(pc.getId(), 230));
				break;
			}
			S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(),
					ActionCodes.ACTION_SkillBuff);
			_attacker.broadcastPacket(gfx);
			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		}

		return usePoly;
	}

	private boolean magicAttack(int idx) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillid = _mobSkillTemplate.getSkillId(idx);
		boolean canUseSkill = false;

		L1Character target = _target.get();
		if (skillid > 0) {
			canUseSkill = skillUse.checkUseSkill(null, skillid,
					target.getId(), target.getX(), target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (_mobSkillTemplate.getLeverage(idx) > 0) {
				skillUse.setLeverage(_mobSkillTemplate.getLeverage(idx));
			}
			skillUse.handleCommands(null, skillid, target.getId(),
					target.getX(), target.getX(), null, 0,
					L1SkillUse.TYPE_NORMAL,	_attacker);
			L1Skill skill = SkillTable.getInstance().findBySkillId(skillid);
			if (skill.getTarget().equals("attack") && skillid != 18) { 
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
			} else {
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			}

			return true;
		}
		return false;
	}

	private boolean physicalAttack(int idx) {
		Map<Integer, Integer> targetList = new ConcurrentHashMap<Integer, Integer>();
		int areaWidth = _mobSkillTemplate.getAreaWidth(idx);
		int areaHeight = _mobSkillTemplate.getAreaHeight(idx);
		int range = _mobSkillTemplate.getRange(idx);
		int actId = _mobSkillTemplate.getActid(idx);
		int gfxId = _mobSkillTemplate.getGfxid(idx);
		L1Character target = _target.get();
		if (_attacker.getLocation().getTileLineDistance(target.getLocation()) > range) {
			return false;
		}

		if (!_attacker.glanceCheck(target.getX(), target.getY())) {
			return false;
		}

		_attacker.setHeading(_attacker.targetDirection(target.getX(),
				target.getY())); 

		if (areaHeight > 0) {
			ArrayList<L1Object> objs =
					L1World.getInstance().getVisibleBoxObjects(_attacker,
							_attacker.getHeading(), areaWidth, areaHeight);

			for (L1Object obj : objs) {
				if (!(obj instanceof L1Character)) {
					continue;
				}

				L1Character cha = (L1Character) obj;
				if (cha.isDead()) {
					continue;
				}

				if (cha instanceof L1PcInstance) {
					if (((L1PcInstance) cha).isGhost()) {
						continue;
					}
				}

				if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
					continue;
				}

				if (target instanceof L1PcInstance
						|| target instanceof L1SummonInstance
						|| target instanceof L1PetInstance) {
					// 
					if (obj instanceof L1PcInstance
							&& !((L1PcInstance) obj).isGhost()
							&& !((L1PcInstance) obj).isGmInvis()
							|| obj instanceof L1SummonInstance
							|| obj instanceof L1PetInstance) {
						targetList.put(obj.getId(), 0);
					}
				} else {
					if (obj instanceof L1MonsterInstance) {
						targetList.put(obj.getId(), 0);
					}
				}
			}
		} else {
			targetList.put(target.getId(), 0); 
		}

		if (targetList.size() == 0) {
			return false;
		}

		Iterator<Integer> ite = targetList.keySet().iterator();
		while (ite.hasNext()) {
			int targetId = ite.next();
			L1Attack attack = new L1Attack(_attacker, (L1Character) L1World
					.getInstance().findObject(targetId));
			if (target.hasSkillEffect(COUNTER_BARRIER)) {
				L1Magic magic = new L1Magic(target, _attacker);
				if (magic.calcProbabilityMagic(COUNTER_BARRIER) &&
						_attacker.getLocation().getTileLineDistance(target.getLocation()) <= 2 &&
						gfxId == 0) {
					attack.actionCounterBarrier();
					attack.commitCounterBarrier();
					continue;
				}
			}
			if (attack.calcHit()) {
				if (_mobSkillTemplate.getLeverage(idx) > 0) {
					attack.setLeverage(_mobSkillTemplate.getLeverage(idx));
				}
				attack.calcDamage();
			}
			if (actId > 0) {
				attack.setActId(actId);
			}
			if (targetId == target.getId()) {
				if (gfxId > 0) {
					_attacker.broadcastPacket(new S_SkillSound(_attacker
							.getId(), gfxId));
				}
				attack.action();
			}
			attack.commit();
		}

		_sleepTime = _attacker.getAtkspeed();
		return true;
	}

	private boolean isSkillUseble(int skillIdx) {
		boolean usable = false;
		L1Character target = _target.get();

		if (_mobSkillTemplate.getTriggerRandom(skillIdx) > 0) {
			int chance = _rnd.nextInt(100) + 1;
			if (chance < _mobSkillTemplate.getTriggerRandom(skillIdx)) {
				usable = true;
			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerHp(skillIdx) > 0) {
			int hpRatio = (_attacker.getCurrentHp() * 100)
					/ _attacker.getMaxHp();
			if (hpRatio <= _mobSkillTemplate.getTriggerHp(skillIdx)) {
				usable = true;
			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerCompanionHp(skillIdx) > 0) {
			L1NpcInstance companionNpc = searchMinCompanionHp();
			if (companionNpc == null) {
				return false;
			}

			int hpRatio = (companionNpc.getCurrentHp() * 100)
					/ companionNpc.getMaxHp();
			if (hpRatio <= _mobSkillTemplate.getTriggerCompanionHp(skillIdx)) {
				usable = true;
				target = companionNpc;
				// TODO: might not need this.
				_target = new WeakReference<L1Character>(companionNpc);
			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerRange(skillIdx) != 0) {
			int distance = _attacker.getLocation().getTileLineDistance(
					target.getLocation());

			if (_mobSkillTemplate.isTriggerDistance(skillIdx, distance)) {
				usable = true;
			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerCount(skillIdx) > 0) {
			if (getSkillUseCount(skillIdx) < _mobSkillTemplate.getTriggerCount(skillIdx)) {
				usable = true;
			} else {
				return false;
			}
		}
		return usable;
	}

	private L1NpcInstance searchMinCompanionHp() {
		L1NpcInstance npc;
		L1NpcInstance minHpNpc = null;
		int hpRatio = 100;
		int companionHpRatio;
		int family = _attacker.getNpcTemplate().get_family();

		for (L1Object object : L1World.getInstance().getVisibleObjects(
				_attacker)) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_family() == family) {
					companionHpRatio = (npc.getCurrentHp() * 100) / npc.getMaxHp();
					if (companionHpRatio < hpRatio) {
						hpRatio = companionHpRatio;
						minHpNpc = npc;
					}
				}
			}
		}
		return minHpNpc;
	}

	private void mobspawn(int summonId, int count) {
		for (int i = 0; i < count; i++)
			mobspawn(summonId);
	}

	private void mobspawn(int summonId) {
		try {
			L1Npc spawnmonster = NpcTable.getInstance().getTemplate(summonId);
			if (spawnmonster == null)
				return;

			L1NpcInstance mob = null;
			try {
				String implementationName = spawnmonster.getImpl();
				Constructor _constructor = Class.forName(
						(new StringBuilder()).append(
							"l1j.server.server.model.Instance.")
						.append(implementationName).append(
							"Instance").toString())
					.getConstructors()[0];
				mob = (L1NpcInstance) _constructor
					.newInstance(new Object[] { spawnmonster });
				mob.setId(IdFactory.getInstance().nextId());
				L1Location loc = _attacker.getLocation().randomLocation(8,
						false);
				int heading = _rnd.nextInt(8);
				mob.setX(loc.getX());
				mob.setY(loc.getY());
				mob.setHomeX(loc.getX());
				mob.setHomeY(loc.getY());
				short mapid = _attacker.getMapId();
				mob.setMap(mapid);
				mob.setHeading(heading);
				L1World.getInstance().storeObject(mob);
				L1World.getInstance().addVisibleObject(mob);
				L1Object object = L1World.getInstance().findObject(
						mob.getId());
				L1MonsterInstance newnpc = (L1MonsterInstance) object;
				newnpc.set_storeDroped(true);
				if (summonId == 45061
						|| summonId == 45161
						|| summonId == 45181
						|| summonId == 45455) {
					newnpc.broadcastPacket(new S_DoActionGFX(
								newnpc.getId(), ActionCodes.ACTION_Hide));
					newnpc.setStatus(13);
					newnpc.broadcastPacket(new S_NPCPack(newnpc));
					newnpc.broadcastPacket(new S_DoActionGFX(
								newnpc.getId(), ActionCodes.ACTION_Appear));
					newnpc.setStatus(0);
					newnpc.broadcastPacket(new S_NPCPack(newnpc));
						}
				newnpc.onNpcAI();
				newnpc.turnOnOffLight();
				newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private L1Character changeTarget(int type, int idx) {
		L1Character target = _target.get();

		switch (type) {
		case L1MobSkill.CHANGE_TARGET_ME:
			target = _attacker;
			break;
		case L1MobSkill.CHANGE_TARGET_RANDOM:
			List<L1Character> targetList = new ArrayList<L1Character>();
			for (L1Object obj : L1World.getInstance().getVisibleObjects(
					_attacker)) {
				if (obj instanceof L1PcInstance || obj instanceof L1PetInstance
						|| obj instanceof L1SummonInstance) {
					L1Character cha = (L1Character) obj;

					int distance = _attacker.getLocation().getTileLineDistance(
							cha.getLocation());

					if (!_mobSkillTemplate.isTriggerDistance(idx, distance)) {
						continue;
					}

					if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
						continue;
					}

					if (!_attacker.getHateList().containsKey(cha)) {
						continue;
					}

					if (cha.isDead()) {
						continue;
					}

					if (cha instanceof L1PcInstance) {
						if (((L1PcInstance) cha).isGhost()) {
							continue;
						}
					}
					targetList.add((L1Character) obj);
				}
			}

			if (targetList.size() > 0) {
				int randomSize = targetList.size() * 100;
				int targetIndex = _rnd.nextInt(randomSize) / 100;
				target = targetList.get(targetIndex);
			}
			break;

		default:
			break;
		}
		return target;
	}
}
