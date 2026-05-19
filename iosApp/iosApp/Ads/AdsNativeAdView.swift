import UIKit
import YandexMobileAds

final class AdsNativeAdView: NativeAdView {
    let titleLbl = UILabel()
    let domainLbl = UILabel()
    let bodyLbl = UILabel()
    let sponsoredLbl = UILabel()
    let warningLbl = UILabel()
    let ctaButton: UIButton = {
        var config = UIButton.Configuration.filled()
        config.baseBackgroundColor = .systemBlue
        config.baseForegroundColor = .white
        config.contentInsets = NSDirectionalEdgeInsets(top: 8, leading: 14, bottom: 8, trailing: 14)
        config.cornerStyle = .fixed
        config.background.cornerRadius = 8
        return UIButton(configuration: config)
    }()
    let feedbackBtn = UIButton(type: .system)
    let iconView = UIImageView()
    let mediaContainer = NativeMediaView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        configure()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        configure()
    }

    private func configure() {
        backgroundColor = .white
        layer.cornerRadius = 12
        layer.masksToBounds = true

        titleLbl.font = .systemFont(ofSize: 16, weight: .semibold)
        titleLbl.textColor = .label
        titleLbl.numberOfLines = 1

        domainLbl.font = .systemFont(ofSize: 12)
        domainLbl.textColor = .secondaryLabel

        bodyLbl.font = .systemFont(ofSize: 14)
        bodyLbl.textColor = .label
        bodyLbl.numberOfLines = 3

        sponsoredLbl.font = .systemFont(ofSize: 11)
        sponsoredLbl.textColor = .tertiaryLabel
        warningLbl.font = .systemFont(ofSize: 11)
        warningLbl.textColor = .tertiaryLabel

        ctaButton.configuration?.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { incoming in
            var outgoing = incoming
            outgoing.font = .systemFont(ofSize: 13, weight: .semibold)
            return outgoing
        }

        iconView.contentMode = .scaleAspectFit
        iconView.layer.cornerRadius = 8
        iconView.layer.masksToBounds = true

        let titleStack = UIStackView(arrangedSubviews: [titleLbl, domainLbl])
        titleStack.axis = .vertical
        titleStack.spacing = 2

        let headerStack = UIStackView(arrangedSubviews: [iconView, titleStack, feedbackBtn])
        headerStack.axis = .horizontal
        headerStack.alignment = .center
        headerStack.spacing = 10
        iconView.translatesAutoresizingMaskIntoConstraints = false
        feedbackBtn.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            iconView.widthAnchor.constraint(equalToConstant: 48),
            iconView.heightAnchor.constraint(equalToConstant: 48),
            feedbackBtn.widthAnchor.constraint(equalToConstant: 24),
            feedbackBtn.heightAnchor.constraint(equalToConstant: 24),
        ])

        let footer = UIStackView(arrangedSubviews: [sponsoredLbl, warningLbl, ctaButton])
        footer.axis = .horizontal
        footer.spacing = 8
        footer.alignment = .center

        let main = UIStackView(arrangedSubviews: [headerStack, mediaContainer, bodyLbl, footer])
        main.axis = .vertical
        main.spacing = 10
        main.translatesAutoresizingMaskIntoConstraints = false
        addSubview(main)

        mediaContainer.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            mediaContainer.heightAnchor.constraint(equalToConstant: 180),
            main.topAnchor.constraint(equalTo: topAnchor, constant: 12),
            main.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 12),
            main.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -12),
            main.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12),
        ])

        titleLabel = titleLbl
        domainLabel = domainLbl
        bodyLabel = bodyLbl
        sponsoredLabel = sponsoredLbl
        warningLabel = warningLbl
        callToActionButton = ctaButton
        feedbackButton = feedbackBtn
        iconImageView = iconView
        mediaView = mediaContainer
    }
}
