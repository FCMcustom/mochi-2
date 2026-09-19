import React, { useState } from 'react';
import { X, Send, Sparkles, Bot, User, HelpCircle, Loader2 } from 'lucide-react';

interface SocraticChatModalProps {
  isOpen: boolean;
  onClose: () => void;
  experimentContext: string;
  studentHypothesis: string;
  safetyViolationPrompt?: string;
}

interface ChatMessage {
  sender: 'ai' | 'user';
  text: string;
}

export const SocraticChatModal: React.FC<SocraticChatModalProps> = ({
  isOpen,
  onClose,
  experimentContext,
  studentHypothesis,
  safetyViolationPrompt,
}) => {
  const [messages, setMessages] = useState<ChatMessage[]>(() => {
    if (safetyViolationPrompt) {
      return [
        {
          sender: 'ai',
          text: `🚨 CẢNH BÁO AN TOÀN PHÒNG THÍ NGHIỆM GDPT 2018: ${safetyViolationPrompt}\n\nThầy phát hiện thao tác vừa rồi đã vi phạm nguyên tắc bảo đảm an toàn thực nghiệm nghiêm ngặt. Em hãy suy nghĩ xem: Khối lượng riêng của axit H2SO4 đặc so với nước như thế nào, và nhiệt lượng tỏa ra khi hiđrat hóa sẽ tích tụ ở đâu?`,
        },
      ];
    }
    return [
      {
        sender: 'ai',
        text: 'Chào em! Thầy là Trợ lý Socratic Hóa học GDPT 2018. Thầy sẽ không trực tiếp đưa ra đáp án, mà sẽ gợi mở các câu hỏi định hướng về dãy điện hóa, bản chất electron và điều kiện phản ứng để em tự mình khám phá. Em đang băn khoăn điều gì ở thí nghiệm này?',
      },
    ];
  });
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);

  // Update initial message if safetyViolationPrompt is provided on open
  React.useEffect(() => {
    if (safetyViolationPrompt && isOpen) {
      setMessages((prev) => {
        const alreadyHasAlert = prev.some((m) => m.text.includes(safetyViolationPrompt));
        if (alreadyHasAlert) return prev;
        return [
          ...prev,
          {
            sender: 'ai',
            text: `🚨 CẢNH BÁO AN TOÀN GDPT 2018: ${safetyViolationPrompt}\n\nTại sao ta bắt buộc phải rót từ từ axit sunfuric đặc dọc theo đũa thủy tinh vào chậu nước mà TUYỆT ĐỐI KHÔNG ĐƯỢC làm ngược lại? Khối lượng riêng và hiệu ứng nhiệt đóng vai trò gì ở đây?`,
          },
        ];
      });
    }
  }, [safetyViolationPrompt, isOpen]);

  if (!isOpen) return null;

  const quickQuestions = safetyViolationPrompt
    ? [
        'Tại sao phải rót từ từ axit vào nước mà tuyệt đối không làm ngược lại?',
        'Khối lượng riêng của H2SO4 đặc nặng hơn hay nhẹ hơn nước?',
        'Enthalpy hòa tan của H2SO4 đặc tỏa ra có làm sôi cục bộ lớp nước không?',
        'Cách sơ cứu chuẩn khi bị axit bắn vào da là gì?',
      ]
    : [
        'Tại sao mẩu kẽm sủi bọt trong HCl?',
        'Đồng có phản ứng được với HCl không và vì sao?',
        'Bản chất chuyển dịch electron vi mô là gì?',
        'Phản ứng này tỏa nhiệt hay thu nhiệt?',
      ];

  const handleSendMessage = async (msgToSend?: string) => {
    const text = (msgToSend || inputText).trim();
    if (!text || loading) return;

    const newMessages: ChatMessage[] = [...messages, { sender: 'user', text }];
    setMessages(newMessages);
    if (!msgToSend) setInputText('');
    setLoading(true);

    try {
      const res = await fetch('/api/socratic/consult', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userMessage: text,
          experimentContext,
          studentHypothesis,
        }),
      });

      const data = await res.json();
      setMessages([...newMessages, { sender: 'ai', text: data.text || 'Gợi ý từ trợ lý Socratic.' }]);
    } catch (err) {
      console.error('Failed to get Socratic response:', err);
      setMessages([
        ...newMessages,
        {
          sender: 'ai',
          text: '💡 Em hãy quan sát xem trong phản ứng này nguyên tử nào đã nhường electron và ion nào nhận electron nhé! Hãy nhớ lại vị trí trong dãy hoạt động hóa học!',
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-sm animate-fadeIn">
      <div className="relative w-full max-w-lg bg-[#0b172d] border border-cyan-500/30 rounded-2xl shadow-2xl flex flex-col h-[560px] overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-3 border-b border-slate-800 bg-slate-900/80">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-cyan-500/20 border border-cyan-400 flex items-center justify-center">
              <Bot className="w-4 h-4 text-cyan-400" />
            </div>
            <div>
              <h3 className="font-bold text-sm text-slate-100 flex items-center gap-1.5">
                <span>Trợ lý Socratic GDPT 2018</span>
                <span className="text-[10px] bg-cyan-950 text-cyan-300 px-1.5 py-0.5 rounded border border-cyan-500/40">
                  AI Socratic
                </span>
              </h3>
              <p className="text-[11px] text-slate-400">
                Gợi mở tư duy khoa học qua phương pháp Vấn đáp Socratic
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-100 hover:bg-slate-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Experiment Context Banner */}
        <div className="bg-slate-900/90 px-4 py-2 border-b border-slate-800 text-[11px] text-slate-400 flex items-center justify-between">
          <span className="truncate">
            Thí nghiệm hiện tại: <strong className="text-cyan-300">{experimentContext}</strong>
          </span>
        </div>

        {/* Message Log */}
        <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-3">
          {messages.map((m, idx) => (
            <div
              key={idx}
              className={`flex gap-2.5 max-w-[85%] ${
                m.sender === 'user' ? 'ml-auto flex-row-reverse' : 'mr-auto'
              }`}
            >
              <div
                className={`w-7 h-7 rounded-full flex items-center justify-center shrink-0 text-xs font-bold ${
                  m.sender === 'user'
                    ? 'bg-cyan-500 text-slate-950'
                    : 'bg-slate-800 border border-cyan-500/30 text-cyan-300'
                }`}
              >
                {m.sender === 'user' ? <User className="w-3.5 h-3.5" /> : <Bot className="w-3.5 h-3.5" />}
              </div>

              <div
                className={`p-3 rounded-2xl text-xs leading-relaxed ${
                  m.sender === 'user'
                    ? 'bg-cyan-600 text-white rounded-tr-none'
                    : 'bg-slate-900 border border-slate-800 text-slate-200 rounded-tl-none whitespace-pre-line'
                }`}
              >
                {m.text}
              </div>
            </div>
          ))}

          {loading && (
            <div className="flex gap-2.5 max-w-[85%] mr-auto">
              <div className="w-7 h-7 rounded-full bg-slate-800 border border-cyan-500/30 flex items-center justify-center">
                <Loader2 className="w-3.5 h-3.5 text-cyan-400 animate-spin" />
              </div>
              <div className="p-3 rounded-2xl bg-slate-900 border border-slate-800 text-xs text-slate-400 italic">
                Trợ lý Socratic đang suy nghĩ câu hỏi gợi mở...
              </div>
            </div>
          )}
        </div>

        {/* Quick Suggestion Chips */}
        <div className="px-3 py-2 bg-slate-900/60 border-t border-slate-800 flex items-center gap-1.5 overflow-x-auto no-scrollbar">
          <HelpCircle className="w-3.5 h-3.5 text-slate-400 shrink-0" />
          {quickQuestions.map((q, idx) => (
            <button
              key={idx}
              onClick={() => handleSendMessage(q)}
              className="text-[10px] px-2.5 py-1 rounded-full bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 whitespace-nowrap transition-colors"
            >
              {q}
            </button>
          ))}
        </div>

        {/* Input Bar */}
        <div className="p-3 bg-slate-900 border-t border-slate-800 flex items-center gap-2">
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
            placeholder="Hỏi trợ lý Socratic về bản chất phản ứng..."
            className="flex-1 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-cyan-400"
          />
          <button
            disabled={loading || !inputText.trim()}
            onClick={() => handleSendMessage()}
            className={`p-2 rounded-xl transition-all ${
              !loading && inputText.trim()
                ? 'bg-cyan-500 text-slate-950 hover:bg-cyan-400'
                : 'bg-slate-800 text-slate-600 cursor-not-allowed'
            }`}
          >
            <Send className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
