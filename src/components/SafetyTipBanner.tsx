import React, { useState, useEffect } from 'react';
import {
  ShieldAlert,
  Lightbulb,
  X,
  ChevronRight,
  Sparkles,
  BookOpen,
  Droplets,
  Flame,
  Wind,
  AlertTriangle,
  RotateCw
} from 'lucide-react';

export interface SafetyTip {
  id: string;
  category: string;
  title: string;
  summary: string;
  actionGuideline: string;
  icon: 'ACID' | 'FIRE' | 'GAS' | 'MERCURY';
  badgeColor: string;
}

export const SAFETY_TIPS: SafetyTip[] = [
  {
    id: 'acid_dilution',
    category: 'Quy tắc pha chế',
    title: 'Pha loãng Axit Sunfuric đặc (H₂SO₄)',
    summary: 'Tuyệt đối KHÔNG ĐƯỢC rót nước vào axit đặc.',
    actionGuideline: 'Luôn rót từ từ axit sunfuric đặc dọc theo đũa thủy tinh vào chậu nước cất và khuấy đều. Nhiệt hydrat hóa cực lớn của H₂SO₄ sẽ gây sôi bùng tức thì làm bắn axit nếu làm ngược lại.',
    icon: 'ACID',
    badgeColor: 'text-rose-400 bg-rose-950/80 border-rose-500/40',
  },
  {
    id: 'sodium_fire',
    category: 'Xử lý sự cố cháy nổ',
    title: 'Hỏa hoạn do Kim loại kiềm (Na, K)',
    summary: 'CẤM TUYỆT ĐỐI dùng nước để dập lửa kim loại kiềm.',
    actionGuideline: 'Natri tác dụng mãnh liệt với nước sinh khí H₂ và tỏa nhiệt gây nổ nguy hiểm. Phải dùng cát khô phủ kín lên ngọn lửa hoặc sử dụng bình chữa cháy dạng bột Class D.',
    icon: 'FIRE',
    badgeColor: 'text-amber-400 bg-amber-950/80 border-amber-500/40',
  },
  {
    id: 'acid_burn',
    category: 'Sơ cứu khẩn cấp',
    title: 'Sơ cứu khi dính Axit đặc lên da',
    summary: 'Rửa ngay dưới vòi nước chảy liên tục trong 15-20 phút.',
    actionGuideline: 'Nước chảy giúp cuốn trôi axit và hạ nhiệt vết thương. Sau đó mới rửa lại bằng dung dịch NaHCO₃ 2% loãng để trung hòa vết axit còn lại. Không bôi kem đánh răng hoặc dầu ăn.',
    icon: 'ACID',
    badgeColor: 'text-rose-400 bg-rose-950/80 border-rose-500/40',
  },
  {
    id: 'toxic_gas',
    category: 'Thao tác an toàn',
    title: 'Thí nghiệm sinh khí độc (NO₂, SO₂, Cl₂)',
    summary: 'Bắt buộc thao tác trong tủ hút (Fume Hood).',
    actionGuideline: 'Khi cần nhận biết mùi khí, không ghé sát mũi ngửi trực tiếp. Đặt ống nghiệm cách xa 20-30cm và dùng lòng bàn tay phẩy nhẹ luồng khí trên miệng ống nghiệm về phía mũi.',
    icon: 'GAS',
    badgeColor: 'text-purple-400 bg-purple-950/80 border-purple-500/40',
  },
  {
    id: 'mercury_spill',
    category: 'Thu gom chất độc',
    title: 'Thu hồi Thủy ngân (Hg) khi vỡ nhiệt kế',
    summary: 'Rắc bột lưu huỳnh (S) phủ kín các hạt thủy ngân rơi vãi.',
    actionGuideline: 'Thủy ngân phản ứng êm dịu ngay ở nhiệt độ thường với lưu huỳnh tạo HgS kết tủa rắn không bay hơi, ngăn chặn hoàn toàn nguy cơ ngộ độc hơi thủy ngân qua đường hô hấp.',
    icon: 'MERCURY',
    badgeColor: 'text-cyan-400 bg-cyan-950/80 border-cyan-500/40',
  },
  {
    id: 'base_burn',
    category: 'Sơ cứu hóa chất',
    title: 'Xử trí khi dính Bazơ kiềm mạnh (NaOH, KOH)',
    summary: 'Xả sạch với thật nhiều nước, rồi rửa dung dịch Axit Axetic 1%.',
    actionGuideline: 'Xút ăn mòn da và phá hủy protein nhanh chóng. Sau khi xả sạch bằng vòi nước chảy, rửa lại bằng axit axetic 1% (hoặc nước cốt chanh pha loãng) để trung hòa môi trường kiềm.',
    icon: 'ACID',
    badgeColor: 'text-emerald-400 bg-emerald-950/80 border-emerald-500/40',
  }
];

interface SafetyTipBannerProps {
  onOpenSafetyModal: () => void;
  onAskSocratic?: (prompt: string) => void;
}

export const SafetyTipBanner: React.FC<SafetyTipBannerProps> = ({
  onOpenSafetyModal,
  onAskSocratic,
}) => {
  // Check session storage to allow dismissing per session or showing on first load
  const [isVisible, setIsVisible] = useState(() => {
    try {
      return sessionStorage.getItem('dismissed_lab_safety_tip') !== 'true';
    } catch {
      return true;
    }
  });

  // Pick deterministic tip based on day of year, but allow cycling
  const [tipIndex, setTipIndex] = useState(() => {
    const dayOfYear = Math.floor(
      (Date.now() - new Date(new Date().getFullYear(), 0, 0).getTime()) / (1000 * 60 * 60 * 24)
    );
    return Math.abs(dayOfYear) % SAFETY_TIPS.length;
  });

  const currentTip = SAFETY_TIPS[tipIndex];

  const handleDismiss = () => {
    setIsVisible(false);
    try {
      sessionStorage.setItem('dismissed_lab_safety_tip', 'true');
    } catch {}
  };

  const handleNextTip = () => {
    setTipIndex((prev) => (prev + 1) % SAFETY_TIPS.length);
  };

  if (!isVisible) return null;

  const renderIcon = () => {
    switch (currentTip.icon) {
      case 'FIRE':
        return <Flame className="w-4 h-4 text-amber-400 shrink-0" />;
      case 'GAS':
        return <Wind className="w-4 h-4 text-purple-400 shrink-0" />;
      case 'MERCURY':
        return <AlertTriangle className="w-4 h-4 text-cyan-400 shrink-0" />;
      case 'ACID':
      default:
        return <Droplets className="w-4 h-4 text-rose-400 shrink-0" />;
    }
  };

  return (
    <aside
      aria-label="Quy tắc an toàn trong ngày"
      className="bg-gradient-to-r from-[#0b172d] via-[#0f2347] to-[#0b172d] border border-amber-500/40 rounded-2xl p-3.5 sm:p-4 shadow-xl relative overflow-hidden transition-all animate-fadeIn"
    >
      {/* Decorative accent glow */}
      <div className="absolute top-0 left-0 w-1.5 h-full bg-gradient-to-b from-amber-400 via-cyan-400 to-amber-500" />

      <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 pl-2 sm:pl-3">
        {/* Main Content Info */}
        <div className="flex items-start gap-3 flex-1 min-w-0">
          <div className="w-9 h-9 rounded-xl bg-amber-500/20 border border-amber-500/30 flex items-center justify-center shrink-0 mt-0.5">
            <ShieldAlert className="w-5 h-5 text-amber-400" />
          </div>

          <div className="flex flex-col gap-1 min-w-0">
            <div className="flex items-center gap-2 flex-wrap">
              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider bg-amber-950 text-amber-300 border border-amber-500/40">
                <Lightbulb className="w-3 h-3 text-amber-400" />
                <span>Quy tắc an toàn trong ngày</span>
              </span>

              <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-semibold border ${currentTip.badgeColor}`}>
                {renderIcon()}
                <span>{currentTip.category}</span>
              </span>

              <span className="text-[11px] font-bold text-slate-100 truncate">
                {currentTip.title}
              </span>
            </div>

            <p className="text-xs text-slate-300 leading-relaxed">
              <strong className="text-amber-300 font-semibold">{currentTip.summary} </strong>
              <span className="text-slate-400">{currentTip.actionGuideline}</span>
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-2 shrink-0 self-end md:self-center pt-2 md:pt-0 border-t md:border-t-0 border-slate-800/80 w-full md:w-auto justify-end">
          {/* Cycle through tips */}
          <button
            onClick={handleNextTip}
            className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-slate-900/90 hover:bg-slate-800 border border-slate-700/80 text-[11px] text-slate-300 font-medium transition-colors cursor-pointer"
            title="Xem quy tắc an toàn khác"
          >
            <RotateCw className="w-3 h-3 text-cyan-400" />
            <span className="hidden sm:inline">Mẹo khác</span>
          </button>

          {/* Socratic Assistant Query */}
          {onAskSocratic && (
            <button
              onClick={() =>
                onAskSocratic(
                  `Thầy/Cô có thể giải thích rõ hơn về quy tắc an toàn: "${currentTip.title}" và cơ chế hóa học đằng sau (${currentTip.summary}) theo chuẩn GDPT 2018 không ạ?`
                )
              }
              className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-cyan-950/80 hover:bg-cyan-900 border border-cyan-500/40 text-[11px] text-cyan-300 font-semibold transition-colors cursor-pointer"
              title="Hỏi Socratic AI về quy tắc này"
            >
              <Sparkles className="w-3 h-3 text-cyan-400" />
              <span>Hỏi AI</span>
            </button>
          )}

          {/* Open Full Safety Modal */}
          <button
            onClick={onOpenSafetyModal}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs shadow-md shadow-amber-500/20 transition-all cursor-pointer"
            title="Xem toàn bộ cẩm nang an toàn và tình huống thực hành"
          >
            <BookOpen className="w-3.5 h-3.5" />
            <span>Cẩm nang</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>

          {/* Dismiss button */}
          <button
            onClick={handleDismiss}
            className="p-1.5 rounded-xl text-slate-400 hover:text-slate-100 hover:bg-slate-800/80 transition-colors cursor-pointer"
            title="Đóng thông báo"
            aria-label="Đóng thông báo quy tắc an toàn"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
