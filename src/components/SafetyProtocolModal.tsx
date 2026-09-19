import React, { useState } from 'react';
import {
  X,
  ShieldAlert,
  Flame,
  AlertTriangle,
  HeartPulse,
  Droplets,
  Wind,
  CheckCircle2,
  XCircle,
  HelpCircle,
  BookOpen
} from 'lucide-react';

interface SafetyScenario {
  id: string;
  title: string;
  hazardIcon: 'ACID' | 'FIRE' | 'GAS' | 'MERCURY';
  situation: string;
  options: {
    text: string;
    isCorrect: boolean;
    explanation: string;
  }[];
}

const SAFETY_SCENARIOS: SafetyScenario[] = [
  {
    id: 'acid_spill',
    title: 'Sơ cứu Bỏng Axit đặc (H2SO4 / HNO3)',
    hazardIcon: 'ACID',
    situation: 'Trong lúc rót axit sunfuric đặc, một lượng nhỏ axit bắn vào mu bàn tay học sinh và bắt đầu gây bỏng rát. Thao tác sơ cứu ĐÚNG NHẤT là gì?',
    options: [
      {
        text: 'Dội ngay dung dịch xút mạnh NaOH 10% để trung hòa axit lập tức.',
        isCorrect: false,
        explanation: '❌ SAI HOÀN TOÀN: NaOH là bazơ ăn mòn cực mạnh, phản ứng trung hòa tỏa nhiệt lớn sẽ làm vết bỏng tổn thương nghiêm trọng hơn gấp nhiều lần!'
      },
      {
        text: 'Rửa ngay lập tức dưới vòi nước chảy liên tục 15-20 phút, sau đó rửa bằng NaHCO3 2%.',
        isCorrect: true,
        explanation: '✓ CHÍNH XÁC: Rửa nước chảy liên tục giúp cuốn trôi axit và hạ nhiệt vết thương. Dung dịch NaHCO3 2% có tính kiềm rất nhẹ giúp trung hòa vết axit còn lại an toàn.'
      },
      {
        text: 'Bôi kem đánh răng hoặc dầu ăn phủ kín vết thương rồi băng chặt lại.',
        isCorrect: false,
        explanation: '❌ SAI: Tuyệt đối không bôi các chất dầu mỡ hoặc kem đánh răng vì sẽ giữ nhiệt, tạo môi trường vi khuẩn và gây nhiễm trùng sâu.'
      }
    ]
  },
  {
    id: 'sodium_fire',
    title: 'Hỏa hoạn Kim loại kiềm Natri (Na)',
    hazardIcon: 'FIRE',
    situation: 'Mẩu Natri sót lại trên bàn thí nghiệm tiếp xúc với giọt nước đọng và bốc cháy ngọn lửa màu vàng chói, phát ra tiếng nổ lách tách. Thao tác dập lửa ĐÚNG là gì?',
    options: [
      {
        text: 'Lấy ca nước dội thẳng vào mẩu Natri đang cháy để hạ nhiệt dập lửa.',
        isCorrect: false,
        explanation: '❌ CỰC KỲ NGUY HIỂM: Natri phản ứng mãnh liệt với nước sinh khí H2 và nhiệt lượng khổng lồ gây NỔ BÙNG, bắn kim loại nóng chảy vào mắt và mặt!'
      },
      {
        text: 'Dùng xô cát khô phủ kín lên mẩu Natri đang cháy hoặc dùng bình bột chữa cháy.',
        isCorrect: true,
        explanation: '✓ CHÍNH XÁC: Cát khô cách ly hoàn toàn Natri với oxi không khí và hơi ẩm, dập tắt đám cháy kim loại kiềm an toàn tuyệt đối.'
      },
      {
        text: 'Dùng quạt thổi mạnh để ngọn lửa nhanh tàn.',
        isCorrect: false,
        explanation: '❌ SAI: Thổi gió cung cấp thêm khí Oxi làm đám cháy bùng phát dữ dội hơn.'
      }
    ]
  },
  {
    id: 'toxic_no2',
    title: 'Hít phải Khí độc Nitrogen Dioxide (NO2)',
    hazardIcon: 'GAS',
    situation: 'Khi làm thí nghiệm Cu + HNO3 đặc, học sinh quên kéo cửa tủ hút khiến hít phải một luồng khí màu nâu đỏ có mùi hắc cay nồng, xuất hiện triệu chứng khó thở và rát họng. Cần xử trí thế nào?',
    options: [
      {
        text: 'Đưa ngay nạn nhân ra nơi thoáng mát, cho hít thở hơi cồn hoặc amoniac loãng, nới lỏng cổ áo.',
        isCorrect: true,
        explanation: '✓ CHÍNH XÁC: Thoát khỏi vùng khí độc lập tức, hơi cồn nhẹ hoặc amoniac loãng giúp trung hòa lượng vết axit và làm dịu niêm mạc đường hô hấp.'
      },
      {
        text: 'Cho nạn nhân uống ngay 1 cốc giấm ăn chua để cân bằng cơ thể.',
        isCorrect: false,
        explanation: '❌ SAI: Khí NO2 tạo axit HNO3 trong phổi, uống giấm không có tác dụng và có thể gây sặc đường thở.'
      }
    ]
  },
  {
    id: 'dilute_acid',
    title: 'Quy tắc pha loãng Axit Sunfuric (H2SO4)',
    hazardIcon: 'ACID',
    situation: 'Muốn pha loãng dung dịch H2SO4 đặc 98% thành dung dịch H2SO4 loãng 10%, thao tác nào sau đây là AN TOÀN TUYỆT ĐỐI?',
    options: [
      {
        text: 'Rót nhanh nước cất vào bình chứa axit sunfuric đặc rồi khuấy đều.',
        isCorrect: false,
        explanation: '❌ SAI VÀ NGUY HIỂM: Nước nhẹ hơn nổi lên trên, nhiệt hydrat hóa khổng lồ làm nước sôi tức thì ở bề mặt gây bắn tung tóe axit đặc nóng ra ngoài (Sôi bùng Exothermic Boil)!'
      },
      {
        text: 'Rót từ từ axit đặc dọc theo đũa thủy tinh vào bình chứa sẵn nước cất và khuấy nhẹ đều.',
        isCorrect: true,
        explanation: '✓ CHÍNH XÁC: Luôn "Rót Axit vào Nước, không làm ngược lại". Thể tích nước lớn sẽ hấp thụ và phân tán nhiệt lượng tỏa ra, ngăn ngừa hiện tượng sôi bùng cục bộ.'
      }
    ]
  }
];

interface SafetyProtocolModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenSocratic?: (prompt?: string) => void;
}

export const SafetyProtocolModal: React.FC<SafetyProtocolModalProps> = ({
  isOpen,
  onClose,
  onOpenSocratic,
}) => {
  const [activeTab, setActiveTab] = useState<'GUIDELINES' | 'DRILL'>('GUIDELINES');
  const [scenarioIndex, setScenarioIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);
  const [hasAnswered, setHasAnswered] = useState(false);

  if (!isOpen) return null;

  const currentScenario = SAFETY_SCENARIOS[scenarioIndex];

  const handleSelectAnswer = (index: number) => {
    if (hasAnswered) return;
    setSelectedAnswer(index);
    setHasAnswered(true);
  };

  const handleNextScenario = () => {
    setSelectedAnswer(null);
    setHasAnswered(false);
    setScenarioIndex((prev) => (prev + 1) % SAFETY_SCENARIOS.length);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/80 backdrop-blur-sm animate-fadeIn">
      <div className="bg-[#0b172d] border border-amber-500/40 rounded-2xl w-full max-w-4xl max-h-[92vh] flex flex-col overflow-hidden shadow-2xl">
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-slate-800 flex items-center justify-between bg-slate-900/60">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-amber-500/20 border border-amber-400/40 flex items-center justify-center text-amber-400">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-100 flex items-center gap-2">
                CẨM NANG QUY TẮC AN TOÀN & SƠ CỨU PHÒNG THÍ NGHIỆM
                <span className="text-[10px] px-2 py-0.5 rounded bg-amber-950 text-amber-300 border border-amber-500/30">
                  TIÊU CHUẨN GDPT 2018
                </span>
              </h2>
              <p className="text-xs text-slate-400">
                Quy chuẩn an toàn hóa chất, trang bị bảo hộ lao động và quy trình xử trí sự cố khẩn cấp
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Toggle */}
        <div className="px-5 pt-3 border-b border-slate-800 flex gap-4 bg-slate-900/30">
          <button
            onClick={() => setActiveTab('GUIDELINES')}
            className={`pb-2.5 text-xs font-bold border-b-2 transition-all flex items-center gap-1.5 cursor-pointer ${
              activeTab === 'GUIDELINES'
                ? 'text-amber-400 border-amber-400'
                : 'text-slate-400 border-transparent hover:text-slate-200'
            }`}
          >
            <BookOpen className="w-3.5 h-3.5" />
            <span>Quy Chuẩn An Toàn Chi Tiết</span>
          </button>
          <button
            onClick={() => setActiveTab('DRILL')}
            className={`pb-2.5 text-xs font-bold border-b-2 transition-all flex items-center gap-1.5 cursor-pointer ${
              activeTab === 'DRILL'
                ? 'text-amber-400 border-amber-400'
                : 'text-slate-400 border-transparent hover:text-slate-200'
            }`}
          >
            <HeartPulse className="w-3.5 h-3.5" />
            <span>Diễn Tập Xử Trí Tình Huống Khẩn Cấp ({SAFETY_SCENARIOS.length})</span>
          </button>
        </div>

        {/* Content Area */}
        <div className="p-4 sm:p-5 overflow-y-auto flex flex-col gap-4 text-xs text-slate-300">
          {activeTab === 'GUIDELINES' ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
              {/* Card 1: Acid Safety */}
              <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col gap-2">
                <div className="flex items-center gap-2 text-rose-400 font-bold text-xs">
                  <Droplets className="w-4 h-4" />
                  <span>1. AN TOÀN VỚI AXIT ĐẶC (H2SO4, HNO3, HCl)</span>
                </div>
                <ul className="list-disc list-inside space-y-1 text-slate-400 leading-relaxed">
                  <li><strong>Quy tắc pha loãng:</strong> Luôn rót từ từ axit vào nước dọc theo đũa thủy tinh. <em>Tuyệt đối không rót nước vào axit đặc</em> vì gây sôi bùng (Exothermic Boil).</li>
                  <li><strong>Bỏng axit trên da:</strong> Rửa ngay bằng dòng nước chảy liên tục trong 15–20 phút, sau đó rửa bằng dung dịch NaHCO3 2% loãng.</li>
                  <li><strong>Bỏng axit vào mắt:</strong> Rửa lập tức tại bình rửa mắt khẩn cấp và chuyển ngay đến cơ sở y tế.</li>
                </ul>
              </div>

              {/* Card 2: Alkali Metal Safety */}
              <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col gap-2">
                <div className="flex items-center gap-2 text-amber-400 font-bold text-xs">
                  <Flame className="w-4 h-4" />
                  <span>2. AN TOÀN VỚI KIM LOẠI KIỀM (Na, K)</span>
                </div>
                <ul className="list-disc list-inside space-y-1 text-slate-400 leading-relaxed">
                  <li><strong>Bảo quản:</strong> Bắt buộc ngâm chìm trong dầu hỏa khan hoặc parafin lỏng, đậy nắp kín.</li>
                  <li><strong>Thao tác:</strong> Dùng kẹp gắp khô, dao cắt lấy mẩu nhỏ bằng hạt đậu xanh trên giấy lọc khô thấm hết dầu.</li>
                  <li><strong>Hỏa hoạn do Na:</strong> <em>CẤM DÙNG NƯỚC</em> dập lửa. Dùng cát khô phủ kín hoặc bình chữa cháy bột Class D.</li>
                </ul>
              </div>

              {/* Card 3: Toxic Gas Protocols */}
              <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col gap-2">
                <div className="flex items-center gap-2 text-purple-400 font-bold text-xs">
                  <Wind className="w-4 h-4" />
                  <span>3. KHÍ ĐỘC HẠI (NO2, Cl2, SO2, H2S)</span>
                </div>
                <ul className="list-disc list-inside space-y-1 text-slate-400 leading-relaxed">
                  <li><strong>Môi trường thí nghiệm:</strong> Bắt buộc tiến hành trong tủ hút thông gió (Fume Hood) có hệ thống xử lý khí thải bằng dung dịch kiềm (NaOH loãng).</li>
                  <li><strong>Ngửi mùi hóa chất:</strong> Dùng bàn tay phẩy nhẹ luồng khí trên miệng ống nghiệm vào mũi từ xa, không ghé sát mũi ngửi trực tiếp.</li>
                  <li><strong>Nhiễm độc khí:</strong> Đưa nạn nhân ra nơi thoáng khí, hít thở sâu, nới lỏng quần áo.</li>
                </ul>
              </div>

              {/* Card 4: Base & Mercury Safety */}
              <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col gap-2">
                <div className="flex items-center gap-2 text-cyan-400 font-bold text-xs">
                  <AlertTriangle className="w-4 h-4" />
                  <span>4. KIỀM ĂN DA & THỦY NGÂN RƠI VÃI</span>
                </div>
                <ul className="list-disc list-inside space-y-1 text-slate-400 leading-relaxed">
                  <li><strong>Dính kiềm mạnh (NaOH, KOH):</strong> Rửa nước sạch nhiều lần, sau đó rửa bằng dung dịch axit axetic loãng 1% (hoặc nước cốt chanh pha loãng).</li>
                  <li><strong>Thủy ngân (Hg) rơi vãi do vỡ nhiệt kế:</strong> Rắc bột lưu huỳnh (S) phủ kín lên các hạt thủy ngân. Hg phản ứng ở nhiệt độ thường tạo HgS dạng bột không bay hơi, thu gom an toàn.</li>
                </ul>
              </div>
            </div>
          ) : (
            /* DRILL SCENARIO SECTION */
            <div className="flex flex-col gap-4">
              <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                <span className="font-bold text-amber-400 uppercase">
                  TÌNH HUỐNG {scenarioIndex + 1} / {SAFETY_SCENARIOS.length}: {currentScenario.title}
                </span>
                <span className="text-slate-500 text-[11px]">Chọn phương án xử trí đúng chuẩn</span>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 text-slate-100 font-medium leading-relaxed">
                {currentScenario.situation}
              </div>

              <div className="flex flex-col gap-2.5">
                {currentScenario.options.map((opt, idx) => {
                  let btnStyle = 'bg-slate-900 border-slate-800 text-slate-200 hover:bg-slate-850 hover:border-slate-700';

                  if (hasAnswered) {
                    if (opt.isCorrect) {
                      btnStyle = 'bg-emerald-950/80 border-emerald-500 text-emerald-200 font-bold';
                    } else if (selectedAnswer === idx) {
                      btnStyle = 'bg-rose-950/80 border-rose-500 text-rose-200';
                    }
                  }

                  return (
                    <button
                      key={idx}
                      disabled={hasAnswered}
                      onClick={() => handleSelectAnswer(idx)}
                      className={`p-3 rounded-xl border text-left text-xs transition-all flex items-start gap-2.5 cursor-pointer ${btnStyle}`}
                    >
                      <span className="font-bold font-mono shrink-0">
                        {String.fromCharCode(65 + idx)}.
                      </span>
                      <div className="flex-1">
                        <p className="leading-relaxed">{opt.text}</p>
                        {hasAnswered && (
                          <div className="mt-2 pt-2 border-t border-slate-800/80 text-[11px] font-normal leading-relaxed">
                            {opt.explanation}
                          </div>
                        )}
                      </div>
                    </button>
                  );
                })}
              </div>

              {hasAnswered && (
                <div className="flex justify-end pt-2">
                  <button
                    onClick={handleNextScenario}
                    className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs shadow-md transition-all cursor-pointer"
                  >
                    Tình huống tiếp theo →
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Socratic Assistant Callout */}
          <div className="p-3.5 rounded-xl bg-slate-900/80 border border-slate-800 flex items-center justify-between gap-3 mt-1">
            <div className="flex items-center gap-2 text-[11px] text-slate-400">
              <HelpCircle className="w-4 h-4 text-cyan-400 shrink-0" />
              <span>Cần hỏi thêm về cách xử lý an toàn với hóa chất khác?</span>
            </div>
            {onOpenSocratic && (
              <button
                onClick={() => {
                  onClose();
                  onOpenSocratic('Em muốn tìm hiểu thêm về các quy chuẩn an toàn phòng thí nghiệm theo chương trình Hóa học GDPT 2018.');
                }}
                className="text-xs text-cyan-400 hover:text-cyan-300 font-semibold underline shrink-0 cursor-pointer"
              >
                Hỏi Trợ lý Socratic AI
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
