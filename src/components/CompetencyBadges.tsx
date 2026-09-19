import React, { useState } from 'react';
import { BktSkillState, ExperimentLog } from '../types';
import {
  Award,
  Lock,
  CheckCircle2,
  Sparkles,
  Trophy,
  Zap,
  Atom,
  Flame,
  BatteryCharging,
  Compass,
  Star,
  GraduationCap,
  ChevronRight,
  Filter,
  Check
} from 'lucide-react';
import { audioEngine } from '../utils/audioEngine';

export interface BadgeDefinition {
  id: string;
  nameVi: string;
  category: 'EXPERIMENT' | 'COMPETENCY' | 'MASTERY';
  categoryLabel: string;
  description: string;
  rarity: 'Đồng' | 'Bạc' | 'Vàng' | 'Huyền thoại';
  rarityColor: {
    border: string;
    bg: string;
    text: string;
    glow: string;
  };
  icon: React.ComponentType<{ className?: string }>;
  unlocked: boolean;
  currentValue: number;
  targetValue: number;
  unit: string;
  detailHint: string;
}

interface CompetencyBadgesProps {
  skills: BktSkillState[];
  logs: ExperimentLog[];
}

export const CompetencyBadges: React.FC<CompetencyBadgesProps> = ({ skills, logs }) => {
  const [activeFilter, setActiveFilter] = useState<'ALL' | 'UNLOCKED' | 'LOCKED'>('ALL');
  const [selectedBadge, setSelectedBadge] = useState<BadgeDefinition | null>(null);

  // Derived metrics from logs and skills
  const totalExperiments = logs.length;
  const successfulExperiments = logs.filter((l) => l.hypothesisCorrect).length;
  const averageMastery =
    skills.reduce((sum, s) => sum + s.currentProb, 0) / (skills.length || 1);

  const getSkillProb = (id: string) => {
    const s = skills.find((item) => item.competencyId === id);
    return s ? s.currentProb : 0;
  };

  const metalSeriesProb = getSkillProb('metal_series');
  const redoxMicroProb = getSkillProb('redox_micro');
  const ionExchangeProb = getSkillProb('ion_exchange');
  const thermoProb = getSkillProb('thermo_enthalpy');

  const badges: BadgeDefinition[] = [
    {
      id: 'first_step',
      nameVi: 'Khởi đầu Thực nghiệm',
      category: 'EXPERIMENT',
      categoryLabel: 'Thực nghiệm',
      description: 'Hoàn thành thí nghiệm đầu tiên và ghi chép vào sổ tay',
      rarity: 'Đồng',
      rarityColor: {
        border: 'border-amber-700/60',
        bg: 'from-amber-950/40 to-slate-900',
        text: 'text-amber-500',
        glow: 'group-hover:border-amber-600',
      },
      icon: Compass,
      unlocked: totalExperiments >= 1,
      currentValue: totalExperiments,
      targetValue: 1,
      unit: 'thí nghiệm',
      detailHint: 'Vào phòng thí nghiệm, chọn 2 chất phản ứng và bấm "Kích hoạt phản ứng" rồi hoàn tất bước giải thích.',
    },
    {
      id: 'scientist_hypothesis',
      nameVi: 'Nhà khoa học Chuẩn xác',
      category: 'EXPERIMENT',
      categoryLabel: 'Thực nghiệm',
      description: 'Đưa ra ít nhất 3 giả thuyết khoa học chính xác trước khi phản ứng xảy ra',
      rarity: 'Bạc',
      rarityColor: {
        border: 'border-slate-400/50',
        bg: 'from-slate-800/40 to-slate-900',
        text: 'text-slate-200',
        glow: 'group-hover:border-slate-300',
      },
      icon: CheckCircle2,
      unlocked: successfulExperiments >= 3,
      currentValue: successfulExperiments,
      targetValue: 3,
      unit: 'giả thuyết đúng',
      detailHint: 'Dự đoán chính xác chất có phản ứng hay không ở Bước 1 trước khi quan sát hiện tượng thực tế.',
    },
    {
      id: 'lab_researcher',
      nameVi: 'Kỷ lục gia Nghiên cứu',
      category: 'EXPERIMENT',
      categoryLabel: 'Thực nghiệm',
      description: 'Tích lũy từ 5 thí nghiệm trở lên trong nhật ký học tập',
      rarity: 'Vàng',
      rarityColor: {
        border: 'border-amber-500/50',
        bg: 'from-amber-950/40 to-slate-900',
        text: 'text-amber-400',
        glow: 'group-hover:border-amber-400',
      },
      icon: Trophy,
      unlocked: totalExperiments >= 5,
      currentValue: totalExperiments,
      targetValue: 5,
      unit: 'thí nghiệm',
      detailHint: 'Thử nghiệm đa dạng các cặp chất: kim loại với axit, muối với bazơ hoặc kim loại mạnh đẩy kim loại yếu.',
    },
    {
      id: 'redox_master',
      nameVi: 'Bậc thầy Điện hóa & E°',
      category: 'COMPETENCY',
      categoryLabel: 'Năng lực BKT',
      description: 'Đạt xác suất làm chủ Dãy điện hóa & Thế điện cực chuẩn P(L) ≥ 70%',
      rarity: 'Vàng',
      rarityColor: {
        border: 'border-cyan-500/50',
        bg: 'from-cyan-950/40 to-slate-900',
        text: 'text-cyan-400',
        glow: 'group-hover:border-cyan-400',
      },
      icon: BatteryCharging,
      unlocked: metalSeriesProb >= 0.7,
      currentValue: Math.round(metalSeriesProb * 100),
      targetValue: 70,
      unit: '%',
      detailHint: 'Vận dụng quy tắc alpha và so sánh thế khử chuẩn giữa các cặp oxi hóa - khử trong bài luyện tập BKT.',
    },
    {
      id: 'submicro_expert',
      nameVi: 'Kính hiển vi Lượng tử',
      category: 'COMPETENCY',
      categoryLabel: 'Năng lực BKT',
      description: 'Nắm vững bản chất vi mô & dịch chuyển electron P(L) ≥ 60%',
      rarity: 'Bạc',
      rarityColor: {
        border: 'border-purple-500/50',
        bg: 'from-purple-950/40 to-slate-900',
        text: 'text-purple-400',
        glow: 'group-hover:border-purple-400',
      },
      icon: Atom,
      unlocked: redoxMicroProb >= 0.6,
      currentValue: Math.round(redoxMicroProb * 100),
      targetValue: 60,
      unit: '%',
      detailHint: 'Quan sát chế độ Vi mô (Micro) để theo dõi quá trình nhường nhận e giữa các nguyên tử và ion.',
    },
    {
      id: 'ion_specialist',
      nameVi: 'Chuyên gia Trao đổi Ion',
      category: 'COMPETENCY',
      categoryLabel: 'Năng lực BKT',
      description: 'Thành thạo điều kiện phản ứng trao đổi ion trong dung dịch P(L) ≥ 75%',
      rarity: 'Vàng',
      rarityColor: {
        border: 'border-emerald-500/50',
        bg: 'from-emerald-950/40 to-slate-900',
        text: 'text-emerald-400',
        glow: 'group-hover:border-emerald-400',
      },
      icon: Zap,
      unlocked: ionExchangeProb >= 0.75,
      currentValue: Math.round(ionExchangeProb * 100),
      targetValue: 75,
      unit: '%',
      detailHint: 'Nắm vững điều kiện tạo kết tủa (BaSO₄, AgCl) hoặc khí (CO₂, SO₂) và phương trình ion thu gọn.',
    },
    {
      id: 'thermo_specialist',
      nameVi: 'Nhà Nhiệt động học',
      category: 'COMPETENCY',
      categoryLabel: 'Năng lực BKT',
      description: 'Hiểu sâu sắc về năng lượng phản ứng & biến thiên Enthalpy P(L) ≥ 60%',
      rarity: 'Bạc',
      rarityColor: {
        border: 'border-rose-500/50',
        bg: 'from-rose-950/40 to-slate-900',
        text: 'text-rose-400',
        glow: 'group-hover:border-rose-400',
      },
      icon: Flame,
      unlocked: thermoProb >= 0.6,
      currentValue: Math.round(thermoProb * 100),
      targetValue: 60,
      unit: '%',
      detailHint: 'Giải thích chính xác các phản ứng tỏa nhiệt mạnh (như Mg + HCl) và tính toán biến thiên Enthalpy ΔrH°.',
    },
    {
      id: 'grand_scholar',
      nameVi: 'Học giả Hóa học GDPT 2018',
      category: 'MASTERY',
      categoryLabel: 'Xuất sắc toàn diện',
      description: 'Chỉ số làm chủ năng lực trung bình toàn khóa đạt từ 70% trở lên',
      rarity: 'Huyền thoại',
      rarityColor: {
        border: 'border-amber-400',
        bg: 'from-amber-900/60 via-purple-950/40 to-slate-900',
        text: 'text-amber-300',
        glow: 'group-hover:border-amber-300 ring-1 ring-amber-400/40',
      },
      icon: GraduationCap,
      unlocked: averageMastery >= 0.7,
      currentValue: Math.round(averageMastery * 100),
      targetValue: 70,
      unit: '%',
      detailHint: 'Cân bằng cả 4 chuẩn năng lực Hóa học cốt lõi bằng cách vừa làm thí nghiệm vừa luyện câu hỏi thích ứng.',
    },
  ];

  const unlockedCount = badges.filter((b) => b.unlocked).length;
  const totalCount = badges.length;
  const progressPercent = Math.round((unlockedCount / totalCount) * 100);

  const filteredBadges = badges.filter((b) => {
    if (activeFilter === 'UNLOCKED') return b.unlocked;
    if (activeFilter === 'LOCKED') return !b.unlocked;
    return true;
  });

  const handleBadgeClick = (badge: BadgeDefinition) => {
    setSelectedBadge(badge);
    if (badge.unlocked) {
      audioEngine.playSuccessChime();
    } else {
      audioEngine.playPourSound();
    }
  };

  return (
    <div className="bg-[#0b172d] border border-cyan-500/30 p-5 rounded-2xl shadow-xl flex flex-col gap-4">
      {/* Header with Title & Overall Stats */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 border-b border-slate-800 pb-3.5">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center">
            <Award className="w-4 h-4 text-amber-400" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-slate-100 uppercase tracking-wide flex items-center gap-2">
              <span>HỆ THỐNG HUY HIỆU NĂNG LỰC & THỰC NGHIỆM</span>
              <span className="text-[10px] px-2 py-0.5 rounded-full bg-cyan-950 border border-cyan-500/40 text-cyan-300 font-bold">
                GDPT 2018
              </span>
            </h3>
            <p className="text-xs text-slate-400">
              Mở khóa thành tựu khi đạt mốc số lần thực nghiệm chính xác và xác suất làm chủ năng lực BKT cao
            </p>
          </div>
        </div>

        {/* Unlocked Counter Pill & Progress */}
        <div className="flex items-center gap-3 bg-slate-900/90 px-3 py-1.5 rounded-xl border border-slate-800">
          <div className="text-right">
            <div className="text-[10px] text-slate-400 font-semibold">Tiến độ mở khóa</div>
            <div className="text-sm font-black font-mono text-cyan-400">
              {unlockedCount}/{totalCount} <span className="text-xs text-slate-500">({progressPercent}%)</span>
            </div>
          </div>
          <div className="w-9 h-9 rounded-full bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-300 font-bold text-xs">
            <Star className="w-4 h-4 fill-cyan-400 text-cyan-400" />
          </div>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center justify-between gap-2 flex-wrap">
        <div className="flex items-center gap-1.5 bg-slate-900/90 p-1 rounded-xl border border-slate-800 text-xs">
          <button
            onClick={() => setActiveFilter('ALL')}
            className={`px-3 py-1 rounded-lg font-semibold transition-all cursor-pointer ${
              activeFilter === 'ALL'
                ? 'bg-cyan-500 text-slate-950 shadow-sm'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Tất cả ({totalCount})
          </button>
          <button
            onClick={() => setActiveFilter('UNLOCKED')}
            className={`px-3 py-1 rounded-lg font-semibold transition-all cursor-pointer ${
              activeFilter === 'UNLOCKED'
                ? 'bg-emerald-500 text-slate-950 shadow-sm'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Đã mở ({unlockedCount})
          </button>
          <button
            onClick={() => setActiveFilter('LOCKED')}
            className={`px-3 py-1 rounded-lg font-semibold transition-all cursor-pointer ${
              activeFilter === 'LOCKED'
                ? 'bg-slate-700 text-slate-200 shadow-sm'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Chưa mở ({totalCount - unlockedCount})
          </button>
        </div>

        <span className="text-[11px] text-slate-400">
          Nhấn vào huy hiệu để xem điều kiện và bí quyết đạt được
        </span>
      </div>

      {/* Badges Bento Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
        {filteredBadges.map((badge) => {
          const IconComponent = badge.icon;
          const isUnlocked = badge.unlocked;
          const currentDisplay = isUnlocked
            ? badge.targetValue
            : Math.min(badge.currentValue, badge.targetValue);
          const percent = Math.min(
            100,
            Math.round((badge.currentValue / (badge.targetValue || 1)) * 100)
          );

          return (
            <div
              key={badge.id}
              onClick={() => handleBadgeClick(badge)}
              className={`group relative p-3.5 rounded-2xl border transition-all duration-200 flex flex-col justify-between cursor-pointer ${
                isUnlocked
                  ? `bg-gradient-to-b ${badge.rarityColor.bg} ${badge.rarityColor.border} ${badge.rarityColor.glow} shadow-md shadow-cyan-950/30 hover:-translate-y-0.5`
                  : 'bg-slate-900/50 border-slate-800/80 opacity-75 hover:opacity-100 hover:border-slate-700'
              }`}
            >
              <div>
                {/* Header: Icon & Rarity Tag */}
                <div className="flex items-start justify-between gap-2 mb-2.5">
                  <div
                    className={`w-11 h-11 rounded-xl flex items-center justify-center border transition-all ${
                      isUnlocked
                        ? 'bg-slate-900/90 border-cyan-400/40 shadow-inner'
                        : 'bg-slate-950/80 border-slate-800'
                    }`}
                  >
                    {isUnlocked ? (
                      <IconComponent className={`w-6 h-6 ${badge.rarityColor.text}`} />
                    ) : (
                      <Lock className="w-5 h-5 text-slate-500" />
                    )}
                  </div>

                  <div className="flex flex-col items-end gap-1">
                    <span
                      className={`text-[9px] font-bold px-2 py-0.5 rounded-md border uppercase tracking-wider ${
                        isUnlocked
                          ? 'bg-emerald-950 text-emerald-300 border-emerald-500/40'
                          : 'bg-slate-950 text-slate-500 border-slate-800'
                      }`}
                    >
                      {isUnlocked ? 'ĐÃ ĐẠT' : 'KHÓA'}
                    </span>
                    <span className="text-[10px] text-slate-400 font-medium">
                      Hạng {badge.rarity}
                    </span>
                  </div>
                </div>

                {/* Badge Name & Category */}
                <div className="mb-2">
                  <div className="text-[10px] uppercase font-bold text-cyan-400/80 tracking-wide">
                    {badge.categoryLabel}
                  </div>
                  <h4
                    className={`text-sm font-bold leading-tight ${
                      isUnlocked ? 'text-white' : 'text-slate-300'
                    }`}
                  >
                    {badge.nameVi}
                  </h4>
                  <p className="text-[11px] text-slate-400 mt-1 line-clamp-2 leading-relaxed">
                    {badge.description}
                  </p>
                </div>
              </div>

              {/* Progress Indicator */}
              <div className="pt-2 border-t border-slate-800/80">
                <div className="flex items-center justify-between text-[11px] font-mono mb-1">
                  <span className="text-slate-400">Tiến độ:</span>
                  <span
                    className={`font-bold ${
                      isUnlocked ? 'text-emerald-400' : 'text-cyan-400'
                    }`}
                  >
                    {currentDisplay}/{badge.targetValue} {badge.unit}
                  </span>
                </div>

                <div className="w-full bg-slate-950 h-1.5 rounded-full overflow-hidden border border-slate-800">
                  <div
                    className={`h-full transition-all duration-500 ${
                      isUnlocked
                        ? 'bg-gradient-to-r from-emerald-400 to-cyan-400'
                        : 'bg-cyan-500/70'
                    }`}
                    style={{ width: `${percent}%` }}
                  />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Selected Badge Detail Modal / Drawer Card */}
      {selectedBadge && (
        <div className="p-4 rounded-2xl bg-gradient-to-r from-[#0f2347] to-[#0b172d] border border-cyan-500/50 shadow-2xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 animate-fadeIn">
          <div className="flex items-start gap-3.5">
            <div
              className={`w-12 h-12 rounded-2xl flex items-center justify-center shrink-0 border ${
                selectedBadge.unlocked
                  ? 'bg-cyan-500/20 border-cyan-400 text-cyan-300'
                  : 'bg-slate-900 border-slate-700 text-slate-400'
              }`}
            >
              {selectedBadge.unlocked ? (
                <Check className="w-6 h-6 text-emerald-400" />
              ) : (
                <Lock className="w-6 h-6 text-slate-400" />
              )}
            </div>

            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <span className="text-xs font-bold text-cyan-400 uppercase tracking-wide">
                  Chi tiết huy hiệu:
                </span>
                <span className="text-sm font-bold text-white">
                  {selectedBadge.nameVi}
                </span>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded border ${
                    selectedBadge.unlocked
                      ? 'bg-emerald-950 text-emerald-300 border-emerald-500/40'
                      : 'bg-slate-900 text-slate-400 border-slate-700'
                  }`}
                >
                  {selectedBadge.unlocked ? 'Đã hoàn thành' : 'Đang thực hiện'}
                </span>
              </div>

              <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                <strong>Yêu cầu:</strong> {selectedBadge.description}
              </p>

              <p className="text-xs text-amber-300 mt-1 leading-relaxed bg-amber-950/40 p-2 rounded-xl border border-amber-500/30">
                <span className="font-bold">Bí quyết mở khóa:</span> {selectedBadge.detailHint}
              </p>
            </div>
          </div>

          <button
            onClick={() => setSelectedBadge(null)}
            className="self-end sm:self-center px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-xs font-semibold text-slate-300 border border-slate-700 cursor-pointer"
          >
            Đóng
          </button>
        </div>
      )}
    </div>
  );
};
