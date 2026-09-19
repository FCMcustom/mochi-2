import React, { useState, useEffect } from 'react';
import { ReactionOutcome, Substance } from '../types';
import {
  Lightbulb,
  CheckCircle2,
  AlertCircle,
  Play,
  RotateCcw,
  Sparkles,
  Send,
  HelpCircle,
  ShieldCheck,
  Award,
  Trophy,
} from 'lucide-react';
import { audioEngine } from '../utils/audioEngine';
import { ConfettiSuccess } from './ConfettiSuccess';

interface PedagogicalStepperProps {
  currentStep: number;
  onSetStep: (step: number) => void;
  reactantA: Substance | null;
  reactantB: Substance | null;
  outcome: ReactionOutcome | null;
  isReactionActive: boolean;
  onActivateReaction: () => void;
  onResetReaction: () => void;
  onRecordExperiment: (
    title: string,
    isCorrect: boolean,
    studentExplanation: string,
    aiFeedback: string,
    competencyId: string
  ) => void;
  onOpenSocratic: () => void;
}

export const PedagogicalStepper: React.FC<PedagogicalStepperProps> = ({
  currentStep,
  onSetStep,
  reactantA,
  reactantB,
  outcome,
  isReactionActive,
  onActivateReaction,
  onResetReaction,
  onRecordExperiment,
  onOpenSocratic,
}) => {
  // Step 1: Hypothesis state
  const [predictedWillReact, setPredictedWillReact] = useState<boolean | null>(null);
  const [predictedPhenomenon, setPredictedPhenomenon] = useState<string>('');
  const [predictedEnergy, setPredictedEnergy] = useState<string>('EXO'); // 'EXO' | 'ENDO' | 'NONE'

  // Confetti & Positive Reinforcement state
  const [showConfetti, setShowConfetti] = useState(false);
  const [hasCelebrated, setHasCelebrated] = useState(false);

  // Step 4: Explanation state
  const [studentExplanation, setStudentExplanation] = useState('');
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [evaluationResult, setEvaluationResult] = useState<string | null>(null);

  // Reset celebration and predictions if reactants change
  useEffect(() => {
    setHasCelebrated(false);
    setShowConfetti(false);
    setPredictedWillReact(null);
    setPredictedPhenomenon('');
    setEvaluationResult(null);
    setStudentExplanation('');
  }, [reactantA?.id, reactantB?.id]);

  const canProceedStep1 = predictedWillReact !== null;
  const canActivateStep2 = Boolean(reactantA && reactantB);

  // Determine actual chemical reaction occurrence
  const isActualReaction = Boolean(
    outcome && (outcome.deltaH !== 0 || outcome.hasGas || outcome.hasPrecipitate || (outcome.balancedEquation && !outcome.balancedEquation.includes('Không xảy ra')))
  );

  // Evaluate if student's prediction was correct
  const isReactionCorrect = predictedWillReact !== null && (predictedWillReact === isActualReaction);

  const isPhenomenonCorrect = (() => {
    if (!predictedPhenomenon || !outcome) return false;
    if (!isActualReaction && predictedPhenomenon === 'none') return true;
    if (isActualReaction) {
      if (predictedPhenomenon === 'gas' && outcome.hasGas) return true;
      if (predictedPhenomenon === 'precipitate' && outcome.hasPrecipitate) return true;
      if (predictedPhenomenon === 'violent' && outcome.isViolent) return true;
      if (predictedPhenomenon === 'color' && !outcome.hasGas && !outcome.hasPrecipitate) return true;
    }
    return false;
  })();

  const isEnergyCorrect = (() => {
    if (!predictedEnergy || !outcome || !isActualReaction) return false;
    if (predictedEnergy === 'EXO' && outcome.deltaH < 0) return true;
    if (predictedEnergy === 'ENDO' && outcome.deltaH > 0) return true;
    return false;
  })();

  const isPredictionCorrect = isReactionCorrect;

  const handleActivateReactionStep = () => {
    audioEngine.playPourSound();
    onActivateReaction();
    onSetStep(3); // Auto advance to observation

    if (isPredictionCorrect && !hasCelebrated) {
      setShowConfetti(true);
      setHasCelebrated(true);
      setTimeout(() => {
        audioEngine.playSuccessChime();
      }, 250);
    }
  };

  const handleTriggerCelebration = () => {
    setShowConfetti(false);
    setTimeout(() => {
      setShowConfetti(true);
      audioEngine.playSuccessChime();
    }, 60);
  };

  const handleEvaluateExplanation = async () => {
    if (!studentExplanation.trim()) return;
    setIsEvaluating(true);

    try {
      const res = await fetch('/api/socratic/evaluate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          studentText: studentExplanation,
          outcome: outcome,
        }),
      });

      const data = await res.json();
      const feedback = data.text || 'Hoàn thành ghi nhận giải thích.';
      setEvaluationResult(feedback);

      // Check if hypothesis was correct
      const hypothesisCorrect = isPredictionCorrect;

      const expTitle = `${reactantA?.formula || 'A'} + ${reactantB?.formula || 'B'}`;
      audioEngine.playSuccessChime();
      if (hypothesisCorrect) {
        setShowConfetti(true);
      }

      onRecordExperiment(
        expTitle,
        hypothesisCorrect,
        studentExplanation,
        feedback,
        outcome?.competencyTarget || 'metal_series'
      );
    } catch (err) {
      console.error('Failed to evaluate explanation:', err);
      const fallback = '✓ Đã ghi nhận báo cáo giải thích vào sổ tay thí nghiệm BKT.';
      setEvaluationResult(fallback);
      audioEngine.playSuccessChime();
      if (isPredictionCorrect) {
        setShowConfetti(true);
      }
      onRecordExperiment(
        `${reactantA?.formula} + ${reactantB?.formula}`,
        isPredictionCorrect,
        studentExplanation,
        fallback,
        outcome?.competencyTarget || 'metal_series'
      );
    } finally {
      setIsEvaluating(false);
    }
  };

  return (
    <div className="bg-[#0b172d] border border-cyan-500/20 rounded-2xl p-4 shadow-xl flex flex-col gap-4">
      {/* 4-Step Navigation Tabs */}
      <div className="grid grid-cols-4 gap-2 border-b border-slate-800 pb-3">
        {[
          { num: 1, label: 'Giả thuyết' },
          { num: 2, label: 'Kích hoạt' },
          { num: 3, label: 'Quan sát' },
          { num: 4, label: 'Giải thích BKT' },
        ].map((s) => {
          const isActive = currentStep === s.num;
          const isDone = currentStep > s.num;

          return (
            <button
              key={s.num}
              onClick={() => onSetStep(s.num)}
              className={`flex items-center justify-center gap-1.5 py-2 px-2 rounded-xl text-xs font-bold transition-all ${
                isActive
                  ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/30'
                  : isDone
                  ? 'bg-slate-800/80 text-cyan-300 border border-cyan-500/40'
                  : 'bg-slate-900/60 text-slate-400 border border-slate-800'
              }`}
            >
              <span
                className={`w-4 h-4 rounded-full flex items-center justify-center text-[10px] font-bold ${
                  isActive
                    ? 'bg-slate-950 text-cyan-400'
                    : isDone
                    ? 'bg-cyan-500 text-slate-950'
                    : 'bg-slate-700 text-slate-300'
                }`}
              >
                {isDone ? '✓' : s.num}
              </span>
              <span className="hidden sm:inline">{s.label}</span>
            </button>
          );
        })}
      </div>

      {/* Step Content */}
      <div className="min-h-[140px] flex flex-col justify-center">
        {/* STEP 1: HYPOTHESIS & PREDICTION */}
        {currentStep === 1 && (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Lightbulb className="w-4 h-4 text-amber-400" />
                <h4 className="font-bold text-sm text-cyan-300">
                  BƯỚC 1: XÂY DỰNG GIẢ THUYẾT & DỰ ĐOÁN HIỆN TƯỢNG
                </h4>
              </div>
              <button
                onClick={onOpenSocratic}
                className="text-xs text-amber-300 hover:text-amber-200 flex items-center gap-1 bg-amber-950/40 px-2 py-1 rounded-md border border-amber-500/30 font-medium"
              >
                <HelpCircle className="w-3.5 h-3.5" />
                <span>Hỏi gợi ý Socratic</span>
              </button>
            </div>

            <p className="text-xs text-slate-300">
              Trước khi bắt đầu, hãy vận dụng kiến thức lý thuyết GDPT 2018 để dự đoán:
              Khi trộn <strong className="text-cyan-300">{reactantA?.formula || '(Chất A)'}</strong> với{' '}
              <strong className="text-emerald-300">{reactantB?.formula || '(Chất B)'}</strong>:
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              {/* Question 1: Will react? */}
              <div className="bg-slate-900/80 p-2.5 rounded-xl border border-slate-800">
                <div className="text-[11px] font-semibold text-slate-400 mb-1.5">
                  1. Có xảy ra phản ứng không?
                </div>
                <div className="grid grid-cols-2 gap-1.5">
                  <button
                    onClick={() => setPredictedWillReact(true)}
                    className={`py-1 rounded text-xs font-bold transition-all ${
                      predictedWillReact === true
                        ? 'bg-cyan-500 text-slate-950'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    Có phản ứng
                  </button>
                  <button
                    onClick={() => setPredictedWillReact(false)}
                    className={`py-1 rounded text-xs font-bold transition-all ${
                      predictedWillReact === false
                        ? 'bg-rose-500 text-white'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    Không phản ứng
                  </button>
                </div>
              </div>

              {/* Question 2: Expected phenomenon */}
              <div className="bg-slate-900/80 p-2.5 rounded-xl border border-slate-800">
                <div className="text-[11px] font-semibold text-slate-400 mb-1.5">
                  2. Hiện tượng đặc trưng?
                </div>
                <select
                  value={predictedPhenomenon}
                  onChange={(e) => setPredictedPhenomenon(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-lg p-1.5 text-xs text-slate-200 focus:outline-none focus:border-cyan-400"
                >
                  <option value="">-- Chọn hiện tượng --</option>
                  <option value="gas">Sủi bọt khí bay lên (H2/NO2)</option>
                  <option value="precipitate">Tạo chất kết tủa lắng xuống</option>
                  <option value="violent">Phát tia lửa / bốc cháy (Na)</option>
                  <option value="color">Dung dịch đổi màu</option>
                  <option value="none">Không có hiện tượng gì</option>
                </select>
              </div>

              {/* Question 3: Energy change */}
              <div className="bg-slate-900/80 p-2.5 rounded-xl border border-slate-800">
                <div className="text-[11px] font-semibold text-slate-400 mb-1.5">
                  3. Năng lượng Enthalpy ΔrH°?
                </div>
                <div className="grid grid-cols-2 gap-1.5">
                  <button
                    onClick={() => setPredictedEnergy('EXO')}
                    className={`py-1 rounded text-xs font-bold transition-all ${
                      predictedEnergy === 'EXO'
                        ? 'bg-amber-500 text-slate-950'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    Tỏa nhiệt (ΔH &lt; 0)
                  </button>
                  <button
                    onClick={() => setPredictedEnergy('ENDO')}
                    className={`py-1 rounded text-xs font-bold transition-all ${
                      predictedEnergy === 'ENDO'
                        ? 'bg-blue-500 text-white'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    Thu nhiệt (ΔH &gt; 0)
                  </button>
                </div>
              </div>
            </div>

            <div className="flex justify-end mt-1">
              <button
                disabled={!canProceedStep1}
                onClick={() => onSetStep(2)}
                className={`flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                  canProceedStep1
                    ? 'bg-cyan-500 hover:bg-cyan-400 text-slate-950 shadow-md shadow-cyan-500/20'
                    : 'bg-slate-800 text-slate-500 cursor-not-allowed'
                }`}
              >
                <span>Xác nhận giả thuyết & Sang Bước 2</span>
                <span>→</span>
              </button>
            </div>
          </div>
        )}

        {/* STEP 2: SAFE LAB OPERATION & ACTIVATION */}
        {currentStep === 2 && (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                <h4 className="font-bold text-sm text-cyan-300">
                  BƯỚC 2: QUY TRÌNH AN TOÀN & KÍCH HOẠT PHẢN ỨNG
                </h4>
              </div>
            </div>

            {/* Safety checklist warnings */}
            <div className="bg-slate-900/80 p-3 rounded-xl border border-slate-800 text-xs flex flex-col gap-2">
              <div className="flex items-center gap-2 text-emerald-400 font-semibold">
                <CheckCircle2 className="w-4 h-4" />
                <span>Kiểm tra an toàn phòng lab: Kính bảo hộ, găng tay, kẹp gỗ đã sẵn sàng.</span>
              </div>
              {(reactantA?.isToxicOrDangerous || reactantB?.isToxicOrDangerous) && (
                <div className="flex items-center gap-2 text-rose-400 bg-rose-950/40 p-2 rounded-lg border border-rose-900/60 font-medium">
                  <AlertCircle className="w-4 h-4 shrink-0" />
                  <span>
                    ⚠️ Chú ý: Một trong hai hóa chất có tính nguy hiểm/độc hại cao. Đảm bảo nắp bảo vệ đã đóng kín trước khi kích hoạt!
                  </span>
                </div>
              )}
            </div>

            <div className="flex items-center justify-between mt-2">
              <button
                onClick={() => onSetStep(1)}
                className="text-xs text-slate-400 hover:text-slate-200"
              >
                ← Quay lại sửa giả thuyết
              </button>

              <div className="flex items-center gap-2">
                {isReactionActive ? (
                  <button
                    onClick={() => {
                      setShowConfetti(false);
                      setHasCelebrated(false);
                      onResetReaction();
                    }}
                    className="flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold bg-rose-600 hover:bg-rose-500 text-white shadow-md transition-all cursor-pointer"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Dừng phản ứng & Làm lại</span>
                  </button>
                ) : (
                  <button
                    disabled={!canActivateStep2}
                    onClick={handleActivateReactionStep}
                    className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold transition-all shadow-lg cursor-pointer ${
                      canActivateStep2
                        ? 'bg-gradient-to-r from-cyan-500 to-emerald-500 hover:from-cyan-400 hover:to-emerald-400 text-slate-950 shadow-cyan-500/30'
                        : 'bg-slate-800 text-slate-500 cursor-not-allowed'
                    }`}
                  >
                    <Play className="w-4 h-4 fill-slate-950" />
                    <span>Trộn hóa chất & Kích hoạt phản ứng</span>
                  </button>
                )}
              </div>
            </div>
          </div>
        )}

        {/* STEP 3: OBSERVATION & ANALYSIS */}
        {currentStep === 3 && (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-cyan-400" />
                <h4 className="font-bold text-sm text-cyan-300">
                  BƯỚC 3: QUAN SÁT & ĐỐI CHIẾU HIỆN TƯỢNG VĨ MÔ - VI MÔ
                </h4>
              </div>

              <button
                onClick={onOpenSocratic}
                className="text-xs text-amber-300 hover:text-amber-200 flex items-center gap-1 bg-amber-950/40 px-2 py-1 rounded-md border border-amber-500/30 font-medium cursor-pointer"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>Trợ lý Socratic giải thích</span>
              </button>
            </div>

            {/* Reaction Summary Box */}
            {outcome && (
              <div className="bg-slate-900/90 p-3 rounded-xl border border-cyan-500/30 flex flex-col gap-2">
                <div className="text-xs font-bold text-amber-300 font-mono">
                  {outcome.balancedEquation}
                </div>
                <div className="text-xs text-slate-200 leading-relaxed">
                  <strong>Hiện tượng thực nghiệm:</strong> {outcome.phenomenonVi}
                </div>
              </div>
            )}

            {/* Positive Reinforcement & Hypothesis Comparison Card */}
            {predictedWillReact !== null && outcome && (
              <div
                className={`p-3.5 rounded-2xl border transition-all ${
                  isPredictionCorrect
                    ? 'bg-gradient-to-r from-emerald-950/60 via-[#0a2620] to-[#0b172d] border-emerald-500/50 shadow-lg shadow-emerald-950/40 animate-fadeIn'
                    : 'bg-slate-900/80 border-slate-800 text-slate-300'
                }`}
              >
                {isPredictionCorrect ? (
                  <div className="flex flex-col gap-2.5">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 text-emerald-400 font-bold text-xs">
                        <div className="w-7 h-7 rounded-xl bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center">
                          <Trophy className="w-4 h-4 text-emerald-300" />
                        </div>
                        <span className="uppercase tracking-wider">🎉 DỰ ĐOÁN THỰC NGHIỆM CHÍNH XÁC!</span>
                      </div>

                      <button
                        onClick={handleTriggerCelebration}
                        className="flex items-center gap-1.5 text-[11px] font-semibold text-emerald-300 hover:text-emerald-200 bg-emerald-900/50 hover:bg-emerald-900/80 px-2.5 py-1 rounded-xl border border-emerald-500/30 transition-all cursor-pointer shadow-sm"
                        title="Kích hoạt lại pháo hoa ăn mừng"
                      >
                        <Sparkles className="w-3.5 h-3.5 text-amber-400" />
                        <span>Xem lại pháo hoa 🎉</span>
                      </button>
                    </div>

                    <p className="text-xs text-emerald-100/90 leading-relaxed">
                      <strong>Rất xuất sắc!</strong> Em đã vận dụng đúng quy luật hóa học để dự đoán chính xác diễn biến của phản ứng trước khi thực nghiệm xảy ra.
                    </p>

                    {/* Breakdown accuracy badges */}
                    <div className="flex items-center gap-2 flex-wrap pt-1 border-t border-emerald-500/20 text-[11px]">
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md bg-emerald-900/70 border border-emerald-500/30 text-emerald-300 font-semibold">
                        ✓ Khả năng phản ứng: {predictedWillReact ? 'Có phản ứng' : 'Không phản ứng'} (Chuẩn xác)
                      </span>

                      {isPhenomenonCorrect && (
                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md bg-cyan-900/70 border border-cyan-500/30 text-cyan-300 font-semibold">
                          ✓ Hiện tượng đặc trưng: Khớp thực nghiệm
                        </span>
                      )}

                      {isEnergyCorrect && (
                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md bg-amber-900/70 border border-amber-500/30 text-amber-300 font-semibold">
                          ✓ Enthalpy: Đúng chiều {predictedEnergy === 'EXO' ? 'tỏa nhiệt (ΔH < 0)' : 'thu nhiệt (ΔH > 0)'}
                        </span>
                      )}
                    </div>
                  </div>
                ) : (
                  <div className="flex flex-col gap-1.5 text-xs">
                    <div className="flex items-center gap-2 text-slate-300 font-bold">
                      <Lightbulb className="w-4 h-4 text-amber-400" />
                      <span>ĐỐI CHIẾU VỚI GIẢ THUYẾT BAN ĐẦU</span>
                    </div>
                    <p className="text-slate-400 leading-relaxed">
                      Giả thuyết ban đầu của em: <em>{predictedWillReact ? 'Có phản ứng' : 'Không phản ứng'}</em>, trong khi thực tế phản ứng <em>{isActualReaction ? 'đã diễn ra' : 'không xảy ra'}</em>. Trong nghiên cứu khoa học, việc quan sát hiện tượng thực tế và điều chỉnh giả thuyết là chìa khóa phát triển tư duy!
                    </p>
                  </div>
                )}
              </div>
            )}

            <div className="flex items-center justify-between mt-1">
              <button
                onClick={() => onSetStep(2)}
                className="text-xs text-slate-400 hover:text-slate-200"
              >
                ← Xem lại thao tác
              </button>
              <button
                onClick={() => onSetStep(4)}
                className="flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold bg-cyan-500 hover:bg-cyan-400 text-slate-950 shadow-md shadow-cyan-500/20"
              >
                <span>Sang Bước 4: Giải thích bản chất & Nhận điểm BKT</span>
                <span>→</span>
              </button>
            </div>
          </div>
        )}

        {/* STEP 4: SCIENTIFIC EXPLANATION & BKT ASSESSMENT */}
        {currentStep === 4 && (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Award className="w-4 h-4 text-amber-400" />
                <h4 className="font-bold text-sm text-cyan-300">
                  BƯỚC 4: GIẢI THÍCH BẢN CHẤT & ĐÁNH GIÁ NĂNG LỰC BKT
                </h4>
              </div>
            </div>

            <p className="text-xs text-slate-300">
              Hãy viết lời giải thích ngắn gọn bằng ngôn ngữ của em (Tại sao phản ứng lại xảy ra? Hạt electron di chuyển như thế nào? Năng lượng Enthalpy thay đổi ra sao?):
            </p>

            <div className="flex flex-col gap-2">
              <textarea
                value={studentExplanation}
                onChange={(e) => setStudentExplanation(e.target.value)}
                placeholder="Ví dụ: Kẽm nhường 2e cho ion H+ trong dung dịch tạo thành bọt khí H2 bay lên. Phản ứng này tỏa nhiệt do enthalpy tạo liên kết lớn hơn..."
                rows={3}
                className="w-full bg-slate-900 border border-slate-700 rounded-xl p-3 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-cyan-400 resize-none font-sans"
              />

              <div className="flex items-center justify-between">
                <div className="text-[11px] text-slate-400">
                  Trợ lý AI sẽ chấm điểm tự động dựa trên rubric GDPT 2018 và cập nhật điểm năng lực BKT.
                </div>

                <button
                  disabled={isEvaluating || !studentExplanation.trim()}
                  onClick={handleEvaluateExplanation}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-md ${
                    !isEvaluating && studentExplanation.trim()
                      ? 'bg-gradient-to-r from-amber-500 to-cyan-500 hover:from-amber-400 hover:to-cyan-400 text-slate-950 shadow-cyan-500/20'
                      : 'bg-slate-800 text-slate-500 cursor-not-allowed'
                  }`}
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>{isEvaluating ? 'Đang chấm điểm...' : 'Gửi chấm điểm Socratic'}</span>
                </button>
              </div>
            </div>

            {/* Evaluation Result Feedback */}
            {evaluationResult && (
              <div className="bg-slate-900/90 border border-emerald-500/40 p-3 rounded-xl text-xs text-slate-200 mt-2 flex flex-col gap-1.5 whitespace-pre-line animate-fadeIn">
                <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                  <Sparkles className="w-4 h-4" />
                  <span>KẾT QUẢ ĐÁNH GIÁ NĂNG LỰC SƯ PHẠM</span>
                </div>
                <div className="text-slate-300 text-xs leading-relaxed">
                  {evaluationResult}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Subtle Confetti / Success Celebration on Accurate Prediction */}
      <ConfettiSuccess
        active={showConfetti}
        onComplete={() => setShowConfetti(false)}
        durationMs={3500}
        particleCount={80}
      />
    </div>
  );
};
