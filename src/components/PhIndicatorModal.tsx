import React, { useState } from 'react';
import {
  X,
  Droplets,
  HelpCircle,
  CheckCircle2,
  Sparkles,
  Info,
  Beaker,
  Gauge,
  Layers,
  FlaskConical,
} from 'lucide-react';
import { Substance, ReactionOutcome } from '../types';
import { calculateSolutionPh } from '../utils/phCalculator';

interface PhIndicatorModalProps {
  isOpen: boolean;
  onClose: () => void;
  reactantA: Substance | null;
  reactantB: Substance | null;
  outcome: ReactionOutcome | null;
  isReactionActive: boolean;
  onOpenSocratic?: (prompt: string) => void;
}

export const PhIndicatorModal: React.FC<PhIndicatorModalProps> = ({
  isOpen,
  onClose,
  reactantA,
  reactantB,
  outcome,
  isReactionActive,
  onOpenSocratic,
}) => {
  const [activeTest, setActiveTest] = useState<'METER' | 'LITMUS' | 'PHENOLPHTHALEIN'>('METER');
  const [isDipped, setIsDipped] = useState(true);
  const [phenolphthaleinDrops, setPhenolphthaleinDrops] = useState(2);
  const [selectedQuizAnswer, setSelectedQuizAnswer] = useState<number | null>(null);

  if (!isOpen) return null;

  const analysis = calculateSolutionPh(reactantA, reactantB, outcome, isReactionActive);

  // Benchmarks for the GDPT 2018 pH Spectrum
  const PH_SPECTRUM_BENCHMARKS = [
    { ph: 0, label: 'HCl 1M, Axit dạ dày', color: '#dc2626' },
    { ph: 2, label: 'Nước cốt chanh, giấm', color: '#ea580c' },
    { ph: 4, label: 'Nước ép cà chua', color: '#f59e0b' },
    { ph: 6, label: 'Sữa tươi, nước mưa', color: '#eab308' },
    { ph: 7, label: 'Nước cất tinh khiết', color: '#22c55e' },
    { ph: 8, label: 'Máu người (7.4), NaHCO3', color: '#10b981' },
    { ph: 10, label: 'Bột nở, xà phòng tắm', color: '#06b6d4' },
    { ph: 12, label: 'Nước vôi trong Ca(OH)2', color: '#3b82f6' },
    { ph: 14, label: 'NaOH 1M, xút tẩy rửa', color: '#7c3aed' },
  ];

  const handleAskSocraticAboutPh = () => {
    const prompt = `Giải thích bản chất giá trị pH = ${analysis.phValue} của hệ phản ứng ${reactantA?.formula || 'A'} + ${reactantB?.formula || 'B'} và cơ chế đổi màu của chỉ thị quỳ tím / phenolphthalein theo chương trình Hóa học 11 GDPT 2018.`;
    if (onOpenSocratic) {
      onOpenSocratic(prompt);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-4xl max-h-[92vh] overflow-y-auto bg-[#0a1324] border border-cyan-500/30 rounded-2xl shadow-2xl flex flex-col text-slate-100">
        {/* Header */}
        <div className="sticky top-0 z-20 flex items-center justify-between p-4 sm:p-5 bg-[#0b162c]/95 border-b border-cyan-500/20 backdrop-blur-md">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-gradient-to-tr from-cyan-500 to-blue-600 text-slate-950 shadow-lg shadow-cyan-500/25">
              <Droplets className="w-5 h-5 fill-slate-950" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base sm:text-lg font-bold text-white tracking-tight">
                  Bộ Phân Tích pH & Chỉ Thị Màu Hóa Học
                </h2>
                <span className="text-[10px] font-mono font-bold px-2 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-500/40">
                  Chuẩn Hóa 11 GDPT 2018
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Khảo sát cân bằng ion trong dung dịch nước • Xác định pH • Chất chỉ thị màu axit - bazơ
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            title="Đóng cửa sổ"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-4 sm:p-6 space-y-6">
          {/* Reaction State Overview Banner */}
          <div className="p-3.5 rounded-xl bg-slate-900/90 border border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
            <div className="flex items-center gap-2">
              <FlaskConical className="w-4 h-4 text-cyan-400" />
              <span className="text-slate-400">Hệ dung dịch đo:</span>
              <span className="font-bold text-white">
                {reactantA?.formula || 'Chưa chọn A'} + {reactantB?.formula || 'Chưa chọn B'}
              </span>
              <span
                className={`ml-2 px-2 py-0.5 rounded-full text-[10px] font-bold ${
                  isReactionActive
                    ? 'bg-emerald-950 text-emerald-300 border border-emerald-500/40'
                    : 'bg-amber-950 text-amber-300 border border-amber-500/40'
                }`}
              >
                {isReactionActive ? '✓ Đang / Đã phản ứng' : 'Chưa kích hoạt phản ứng'}
              </span>
            </div>

            <div className="text-slate-400 italic">
              Nhiệt độ phòng tiêu chuẩn: <span className="font-mono text-cyan-300 font-bold">25°C (298 K)</span>
            </div>
          </div>

          {/* Test Selector Tabs */}
          <div className="flex flex-wrap gap-2 border-b border-slate-800 pb-2">
            {[
              { id: 'METER', label: 'Điện Cực Đo pH Kỹ Thuật Số', icon: Gauge },
              { id: 'LITMUS', label: 'Que Thử Giấy Quỳ Tím', icon: Layers },
              { id: 'PHENOLPHTHALEIN', label: 'Chỉ Thị Phenolphthalein', icon: Droplets },
            ].map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTest === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTest(tab.id as any)}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    isActive
                      ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/20'
                      : 'bg-slate-900 text-slate-400 hover:text-slate-200 border border-slate-800 hover:border-slate-700'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>

          {/* Active Testing Stage */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-5">
            {/* Visual Testing Apparatus Preview */}
            <div className="md:col-span-6 bg-[#081020] rounded-2xl p-5 border border-slate-800 flex flex-col items-center justify-center min-h-[290px] relative overflow-hidden">
              {/* Test Tube Graphic with Solution */}
              <div className="relative w-36 h-60 flex flex-col items-center justify-end">
                {/* Test Tube Rim & Glass Body */}
                <div className="absolute inset-0 border-4 border-slate-500/60 rounded-b-full bg-slate-900/40 backdrop-blur-sm shadow-inner z-10 pointer-events-none">
                  <div className="w-full h-4 border-b-2 border-slate-400/40 bg-slate-400/10" />
                </div>

                {/* Liquid in Test Tube */}
                <div
                  className="w-full rounded-b-full transition-all duration-700 flex items-center justify-center overflow-hidden z-0"
                  style={{
                    height: '68%',
                    backgroundColor:
                      activeTest === 'PHENOLPHTHALEIN' && analysis.phValue >= 8.3
                        ? '#db2777' // Vibrant Magenta Pink for basic phenolphthalein
                        : activeTest === 'PHENOLPHTHALEIN'
                        ? 'rgba(224, 242, 254, 0.4)' // Colorless/pale
                        : analysis.phValue < 4
                        ? 'rgba(239, 68, 68, 0.25)'
                        : analysis.phValue > 10
                        ? 'rgba(37, 99, 235, 0.25)'
                        : 'rgba(56, 189, 248, 0.2)',
                  }}
                >
                  {/* Subtle bubbles/particles */}
                  <div className="text-[10px] font-mono text-white/70 font-semibold px-2 py-1 bg-slate-950/40 rounded backdrop-blur-xs text-center z-10">
                    {activeTest === 'PHENOLPHTHALEIN' && analysis.phValue >= 8.3
                      ? 'Dung dịch hóa hồng (OH⁻)'
                      : `Dung dịch (${analysis.classificationVi})`}
                  </div>
                </div>

                {/* Apparatus Overlays based on activeTest */}
                {activeTest === 'METER' && (
                  <div className="absolute top-2 z-20 flex flex-col items-center animate-in slide-in-from-top-4 duration-300">
                    {/* pH Probe Cable & Glass Electrode */}
                    <div className="w-1.5 h-12 bg-slate-400 rounded-full" />
                    <div className="w-4 h-24 bg-gradient-to-b from-slate-700 via-slate-600 to-amber-200/90 border border-slate-400 rounded-b-full shadow-lg flex items-center justify-center">
                      <div className="w-1.5 h-3 bg-amber-400 rounded-full animate-pulse" />
                    </div>
                  </div>
                )}

                {activeTest === 'LITMUS' && (
                  <div
                    className={`absolute z-20 transition-all duration-500 flex flex-col items-center ${
                      isDipped ? 'top-8' : 'top-0'
                    }`}
                  >
                    {/* Paper Strip (Quỳ tím) */}
                    <div className="w-7 h-36 rounded-sm shadow-xl border border-slate-700/80 flex flex-col overflow-hidden">
                      {/* Dry upper part (Standard purple) */}
                      <div className="w-full h-14 bg-purple-700 flex items-center justify-center">
                        <span className="text-[8px] text-white/80 font-mono font-bold rotate-90">
                          QUỲ TÍM
                        </span>
                      </div>
                      {/* Submerged active tip */}
                      <div
                        className="w-full flex-1 transition-colors duration-500 flex items-center justify-center"
                        style={{ backgroundColor: analysis.litmusColorHex }}
                      >
                        <span className="text-[8px] text-white font-mono font-bold">
                          {analysis.phValue < 5 ? 'ĐỎ' : analysis.phValue > 8 ? 'XANH' : 'TÍM'}
                        </span>
                      </div>
                    </div>
                  </div>
                )}

                {activeTest === 'PHENOLPHTHALEIN' && (
                  <div className="absolute top-1 z-20 flex flex-col items-center animate-in fade-in duration-300">
                    <div className="w-3 h-14 bg-slate-300/80 rounded-t-full border border-slate-400 flex items-end justify-center">
                      <div className="w-1.5 h-1.5 rounded-full bg-pink-400 animate-ping mb-1" />
                    </div>
                    <div className="text-[9px] font-mono text-pink-300 mt-1 font-bold">
                      +{phenolphthaleinDrops} giọt PP
                    </div>
                  </div>
                )}
              </div>

              {/* Action buttons inside stage */}
              <div className="mt-4 flex items-center gap-2 z-20">
                {activeTest === 'LITMUS' && (
                  <button
                    onClick={() => setIsDipped(!isDipped)}
                    className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs text-cyan-300 font-bold border border-slate-700 transition-colors"
                  >
                    {isDipped ? 'Nhấc que quỳ lên' : 'Nhúng que quỳ vào dung dịch'}
                  </button>
                )}

                {activeTest === 'PHENOLPHTHALEIN' && (
                  <button
                    onClick={() => setPhenolphthaleinDrops((d) => d + 1)}
                    className="px-3 py-1.5 rounded-lg bg-pink-950/80 hover:bg-pink-900 border border-pink-500/50 text-xs text-pink-200 font-bold transition-colors flex items-center gap-1.5"
                  >
                    <Droplets className="w-3.5 h-3.5 text-pink-400" />
                    <span>Nhỏ thêm 1 giọt Phenolphthalein</span>
                  </button>
                )}
              </div>
            </div>

            {/* Live Measurements & Chemical Telemetry */}
            <div className="md:col-span-6 flex flex-col justify-between space-y-4">
              {/* Digital pH Display Box */}
              <div className="bg-slate-900/90 rounded-2xl p-4 border border-cyan-500/30 flex items-center justify-between">
                <div>
                  <div className="text-[11px] font-mono uppercase tracking-wider text-slate-400 font-bold">
                    Chỉ số pH Đo Được
                  </div>
                  <div className="text-3xl sm:text-4xl font-mono font-black text-cyan-300 tracking-tight flex items-baseline gap-2 mt-1">
                    <span>{analysis.phValue.toFixed(2)}</span>
                    <span className="text-xs font-sans text-slate-400 font-normal">
                      (pOH = {analysis.pOhValue.toFixed(2)})
                    </span>
                  </div>
                  <div className="mt-1 flex items-center gap-1.5 text-xs font-semibold">
                    <span
                      className="w-2.5 h-2.5 rounded-full"
                      style={{ backgroundColor: analysis.universalColorHex }}
                    />
                    <span className="text-white">{analysis.classificationVi}</span>
                  </div>
                </div>

                {/* Ion Molarities */}
                <div className="text-right font-mono text-[11px] space-y-1 bg-[#070e1c] p-2.5 rounded-xl border border-slate-800">
                  <div className="text-slate-400">
                    [H⁺] ={' '}
                    <span className="text-cyan-400 font-bold">
                      {analysis.hPlusMolarity < 0.001
                        ? analysis.hPlusMolarity.toExponential(2)
                        : analysis.hPlusMolarity.toFixed(4)}{' '}
                      M
                    </span>
                  </div>
                  <div className="text-slate-400">
                    [OH⁻] ={' '}
                    <span className="text-pink-400 font-bold">
                      {analysis.ohMinusMolarity < 0.001
                        ? analysis.ohMinusMolarity.toExponential(2)
                        : analysis.ohMinusMolarity.toFixed(4)}{' '}
                      M
                    </span>
                  </div>
                  <div className="text-[10px] text-slate-500 pt-0.5 border-t border-slate-800">
                    Kw = 1.0 × 10⁻¹⁴ (ở 25°C)
                  </div>
                </div>
              </div>

              {/* Indicator Observation Details */}
              <div className="bg-slate-900/70 rounded-xl p-4 border border-slate-800 space-y-2.5 text-xs">
                <div className="font-bold text-slate-300 flex items-center gap-2">
                  <Info className="w-4 h-4 text-cyan-400" />
                  <span>Kết Quả Kiểm Thử Chất Chỉ Thị:</span>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <div className="p-2.5 rounded-lg bg-slate-950 border border-slate-800">
                    <div className="text-[10px] text-slate-400 font-bold">Giấy Quỳ Tím (Litmus)</div>
                    <div className="flex items-center gap-2 mt-1">
                      <span
                        className="w-3.5 h-3.5 rounded shadow-sm shrink-0"
                        style={{ backgroundColor: analysis.litmusColorHex }}
                      />
                      <span className="text-slate-200 font-semibold leading-tight">
                        {analysis.litmusDescriptionVi}
                      </span>
                    </div>
                  </div>

                  <div className="p-2.5 rounded-lg bg-slate-950 border border-slate-800">
                    <div className="text-[10px] text-slate-400 font-bold">Phenolphthalein</div>
                    <div className="flex items-center gap-2 mt-1">
                      <span
                        className="w-3.5 h-3.5 rounded border border-slate-700 shrink-0"
                        style={{ backgroundColor: analysis.phenolphthaleinColorHex }}
                      />
                      <span className="text-slate-200 font-semibold leading-tight">
                        {analysis.phenolphthaleinDescriptionVi}
                      </span>
                    </div>
                  </div>
                </div>

                <p className="text-[11px] text-slate-400 leading-relaxed bg-[#060c18] p-2 rounded-lg border border-slate-800/80">
                  <span className="text-cyan-400 font-semibold">Cơ sở lý thuyết: </span>
                  {analysis.explanationVi}
                </p>
              </div>

              {/* Socratic Assistant Consultation Trigger */}
              <button
                onClick={handleAskSocraticAboutPh}
                className="w-full py-2.5 px-4 rounded-xl bg-gradient-to-r from-cyan-500/20 to-blue-500/20 hover:from-cyan-500/30 hover:to-blue-500/30 border border-cyan-500/40 text-cyan-300 font-bold text-xs flex items-center justify-center gap-2 transition-all shadow-md cursor-pointer"
              >
                <Sparkles className="w-4 h-4 text-cyan-400" />
                <span>Hỏi Socratic AI về Cân Bằng Ion & pH Phản Ứng Này</span>
              </button>
            </div>
          </div>

          {/* Full Standard pH Spectrum (0 to 14) with GDPT 2018 Benchmarks */}
          <div className="bg-slate-900/80 rounded-2xl p-4 sm:p-5 border border-slate-800 space-y-3">
            <div className="flex items-center justify-between">
              <div className="font-bold text-xs sm:text-sm text-white flex items-center gap-2">
                <Gauge className="w-4 h-4 text-cyan-400" />
                <span>Thang Đo pH & Mẫu Chất Đối Chiếu Tiêu Chuẩn (GDPT 2018)</span>
              </div>
              <span className="text-[11px] font-mono text-slate-400">
                Axit (0 - 6) • Trung tính (7) • Kiềm (8 - 14)
              </span>
            </div>

            {/* Continuous Color Gradient Bar with Indicator Pin */}
            <div className="relative pt-6 pb-2">
              {/* Pointer pin showing current pH */}
              <div
                className="absolute top-0 transform -translate-x-1/2 flex flex-col items-center transition-all duration-500 z-10"
                style={{ left: `${Math.min(100, Math.max(0, (analysis.phValue / 14) * 100))}%` }}
              >
                <span className="text-[10px] font-mono font-black bg-cyan-400 text-slate-950 px-1.5 py-0.5 rounded shadow">
                  pH {analysis.phValue.toFixed(1)}
                </span>
                <div className="w-0 h-0 border-l-[4px] border-l-transparent border-r-[4px] border-r-transparent border-t-[5px] border-t-cyan-400" />
              </div>

              {/* Gradient Track */}
              <div
                className="w-full h-4 rounded-full shadow-inner"
                style={{
                  background:
                    'linear-gradient(to right, #dc2626 0%, #ea580c 14%, #f59e0b 28%, #eab308 42%, #22c55e 50%, #06b6d4 64%, #3b82f6 78%, #7c3aed 92%, #581c87 100%)',
                }}
              />
            </div>

            {/* Benchmarks Grid */}
            <div className="grid grid-cols-3 sm:grid-cols-5 md:grid-cols-9 gap-1.5 pt-1">
              {PH_SPECTRUM_BENCHMARKS.map((item) => (
                <div
                  key={item.ph}
                  className={`p-2 rounded-lg text-center transition-all ${
                    Math.abs(analysis.phValue - item.ph) < 1.0
                      ? 'bg-cyan-950/80 border border-cyan-500/50 shadow-sm'
                      : 'bg-slate-950/60 border border-slate-800/60'
                  }`}
                >
                  <div
                    className="text-xs font-mono font-black inline-block px-1.5 py-0.2 rounded"
                    style={{ color: item.color }}
                  >
                    pH {item.ph}
                  </div>
                  <div className="text-[10px] text-slate-400 mt-1 line-clamp-2 leading-tight">
                    {item.label}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Interactive Self-Assessment Quiz (BKT Diagnostic) */}
          <div className="p-4 rounded-xl bg-gradient-to-r from-slate-900 to-[#0e1b34] border border-cyan-500/20">
            <div className="flex items-center gap-2 font-bold text-xs text-cyan-300 mb-2">
              <HelpCircle className="w-4 h-4 text-cyan-400" />
              <span>Câu hỏi củng cố năng lực Hóa học 11:</span>
            </div>
            <p className="text-xs text-slate-200 mb-3">
              Cho dung dịch axit mạnh HCl nồng độ 0.01 M ở 25°C. Hãy xác định giá trị pH và màu của giấy quỳ tím khi nhúng vào?
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              {[
                {
                  id: 0,
                  text: 'A. pH = 2.0; Quỳ tím hóa đỏ',
                  isCorrect: true,
                  expl: '✓ CHÍNH XÁC: [H+] = 0.01 M = 10⁻² M ⇒ pH = -log(10⁻²) = 2.0. Môi trường axit làm quỳ tím hóa đỏ.',
                },
                {
                  id: 1,
                  text: 'B. pH = 12.0; Quỳ tím hóa xanh',
                  isCorrect: false,
                  expl: '❌ SAI: pH = 12 là môi trường bazơ mạnh. Axit HCl có pH < 7.',
                },
                {
                  id: 2,
                  text: 'C. pH = 7.0; Quỳ tím giữ nguyên màu',
                  isCorrect: false,
                  expl: '❌ SAI: pH = 7 là môi trường trung tính (nước cất). HCl là axit mạnh phân li hoàn toàn.',
                },
              ].map((opt) => (
                <button
                  key={opt.id}
                  onClick={() => setSelectedQuizAnswer(opt.id)}
                  className={`p-2.5 rounded-lg text-left text-xs font-medium border transition-all ${
                    selectedQuizAnswer === opt.id
                      ? opt.isCorrect
                        ? 'bg-emerald-950 border-emerald-500 text-emerald-200'
                        : 'bg-rose-950 border-rose-500 text-rose-200'
                      : 'bg-slate-950/70 border-slate-800 text-slate-300 hover:border-slate-700'
                  }`}
                >
                  {opt.text}
                </button>
              ))}
            </div>

            {selectedQuizAnswer !== null && (
              <div className="mt-2.5 p-2 rounded-lg bg-slate-950/80 border border-slate-800 text-xs">
                {selectedQuizAnswer === 0 ? (
                  <div className="text-emerald-400 flex items-center gap-1.5 font-medium">
                    <CheckCircle2 className="w-4 h-4 shrink-0" />
                    <span>✓ Xuất sắc! Nắm chắc công thức tính pH = -log[H⁺] theo chuẩn GDPT 2018.</span>
                  </div>
                ) : (
                  <div className="text-rose-300 font-medium">
                    ❌ Chưa chính xác. HCl phân li hoàn toàn: HCl → H⁺ + Cl⁻ ⇒ [H⁺] = 0.01 M ⇒ pH = -log(0.01) = 2.0.
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-[#081224] border-t border-slate-800 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold transition-colors cursor-pointer"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
